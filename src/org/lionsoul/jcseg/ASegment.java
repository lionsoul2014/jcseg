package org.lionsoul.jcseg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;

import org.lionsoul.jcseg.core.ADictionary;
import org.lionsoul.jcseg.core.IChunk;
import org.lionsoul.jcseg.core.ILexicon;
import org.lionsoul.jcseg.core.ISegment;
import org.lionsoul.jcseg.core.IWord;
import org.lionsoul.jcseg.core.JcsegTaskConfig;
import org.lionsoul.jcseg.filter.CNNMFilter;
import org.lionsoul.jcseg.filter.ENSCFilter;
import org.lionsoul.jcseg.filter.PPTFilter;
import org.lionsoul.jcseg.util.IStringBuffer;
import org.lionsoul.jcseg.util.IntArrayList;


/**
 * abstract segment class, implemented ISegment interface <br />
 * implements all the common method that 
 * 		simple segment and Complex segment algorithm both share. <br />
 * 
 * @author	chenxin <chenxin619315@gmail.com>
 */
public abstract class ASegment implements ISegment {
	
	/*current position for the given stream.*/
	protected int idx;
	protected PushbackReader reader = null;
	/*CJK word cache poll*/
	protected LinkedList<IWord> wordPool = new LinkedList<IWord>();
	protected IStringBuffer isb;
	protected IntArrayList ialist;
	protected boolean checkCE = false;
	
	/*the dictionary and task config*/
	protected ADictionary dic;
	protected JcsegTaskConfig config;
	
	public ASegment( JcsegTaskConfig config, ADictionary dic ) throws IOException {
		this(null, config, dic);
	}
	
	public ASegment( Reader input, 
				JcsegTaskConfig config, ADictionary dic ) throws IOException {
		this.config = config;
		this.dic = dic;
		isb = new IStringBuffer(64);
		ialist = new IntArrayList(15);
		reset(input);
	}
	
	/**
	 * stream/reader reset.
	 * 
	 * @param input
	 * @throws IOException
	 */
	public void reset( Reader input ) throws IOException { 
		if ( input != null )
			reader = new PushbackReader(new BufferedReader(input), 64);
		idx = -1;
	}
	
	/**
	 * read the next char from the current position 
	 * @throws IOException 
	 */
	protected int readNext() throws IOException {
		int c = reader.read();
		if ( c != -1 ) idx++;
		return c;
	}
	
	/**
	 * push back the data to the stream.
	 * 
	 * @param data
	 * @throws IOException 
	 */
	protected void pushBack( int data ) throws IOException {
		reader.unread(data);
		idx--;
	}
	
	@Override
	public int getStreamPosition() {
		return idx + 1;
	}
	
	/**
	 * set the dictionary of the current segmentor. <br />
	 * 
	 * @param	dic
	 */
	public void setDict( ADictionary dic ) {
		this.dic = dic;
	}
	
	/**
	 * get the current dictionary instance . <br />
	 * 
	 * @return	ADictionary
	 */
	public ADictionary getDict() {
		return dic;
	}
	
	/**
	 * set the current task config . <br />
	 * 
	 * @param	config
	 */
	public void setConfig( JcsegTaskConfig config ) {
		this.config = config;
	}
	
	/**
	 * get the current task config instance. <br /> 
	 * 
	 * @param	JcsegTaskConfig
	 */
	public JcsegTaskConfig getConfig() {
		return config;
	}

	/**
	 * @see ISegment#next() 
	 */
	@Override
	public IWord next() throws IOException {
		if ( wordPool.size() > 0 ) return wordPool.removeFirst(); 
		int c, pos;
		while ( (c = readNext()) != -1 ) {
			if ( ENSCFilter.isWhitespace(c) ) continue;
			pos = idx;
			//System.out.println((char)c);
			if ( isCJKChar( c ) ) 
			{
				char[] chars = nextCJKSentence(c);
				int cjkidx = 0;
				IWord w = null;
				while ( cjkidx < chars.length ) {
					/*
					 * find the next CJK word.
					 * the process will be different with the different algorithm
					 * @see getBestCJKChunk() from SimpleSeg or ComplexSeg. 
					 */
					w = null;
					
					/*
					 * @istep 1: 
					 * 
					 * check if there is chinese numeric
					 */
					if ( CNNMFilter.isCNNumeric(chars[cjkidx]) > -1 
							&& cjkidx + 1 < chars.length ) {
						//get the chinese numeric chars
						String num = nextCNNumeric( chars, cjkidx );
						if (cjkidx + 3 < chars.length && chars[cjkidx+1] == '分' 
									&& chars[cjkidx+2] == '之' 
									&& CNNMFilter.isCNNumeric(chars[cjkidx+3]) > -1  )  {
							w = new Word(num, IWord.T_CN_NUMERIC);
							w.setPosition(pos+cjkidx);
							w.setPartSpeech(IWord.NUMERIC_POSPEECH);
							wordPool.add(w);
							
							/* Here: 
							 * Convert the chinese fraction to arabic fraction,
							 * 		if the Config.CNFRA_TO_ARABIC is true.
							 */
							if ( config.CNFRA_TO_ARABIC ) {
								String[] split = num.split("分之");
								IWord wd = new Word(CNNMFilter.cnNumericToArabic(split[1], true)
										+"/"+CNNMFilter.cnNumericToArabic(split[0], true),
										IWord.T_CN_NUMERIC);
								wd.setPosition(w.getPosition());
								wd.setPartSpeech(IWord.NUMERIC_POSPEECH);
								wordPool.add(wd);
							}
						} else if ( CNNMFilter.isCNNumeric(chars[cjkidx+1]) > -1
								|| dic.match(ILexicon.CJK_UNITS, chars[cjkidx+1]+"")) {
							
							StringBuilder sb = new StringBuilder();
							String temp = null;
							sb.append(num);
							boolean matched = false;
							int j;
							
							//find the word that made up with the numeric
							//like: 五四运动
							for ( j = num.length();
									(cjkidx + j) < chars.length && j < config.MAX_LENGTH; j++ ) {
								sb.append(chars[cjkidx + j]);
								temp = sb.toString();
								if ( dic.match(ILexicon.CJK_WORD, temp) ) {
									w = dic.get(ILexicon.CJK_WORD, temp);
									num = temp;
									matched = true;
								}
							}
							
							IWord wd = null;
							//find the numeric units
							if ( matched == false && config.CNNUM_TO_ARABIC ) {
								//get the numeric'a arabic
								String arbic = CNNMFilter.cnNumericToArabic(num, true)+"";
								
								if ( (cjkidx + num.length()) < chars.length
										&& dic.match(ILexicon.CJK_UNITS,
												chars[cjkidx + num.length()]+"" ) ) {
									char units = chars[ cjkidx + num.length() ];
									num += units; arbic += units;
								}
								
								wd = new Word( arbic, IWord.T_CN_NUMERIC);
								wd.setPartSpeech(IWord.NUMERIC_POSPEECH);
								wd.setPosition(pos+cjkidx);
							}
							//clear the stop words
							if ( dic.match(ILexicon.STOP_WORD, num) ) {
								cjkidx += num.length();
								continue;
							}
							
							if ( w == null ) {
								w = new Word( num, IWord.T_CN_NUMERIC );
								w.setPartSpeech(IWord.NUMERIC_POSPEECH);
							}
							w.setPosition(pos + cjkidx);
							wordPool.add(w);
							if ( wd != null ) wordPool.add(wd);
						}
						
						if ( w != null ) {
							cjkidx += w.getLength();
							//add the pinyin to the poll
							if ( config.APPEND_CJK_PINYIN 
									&& config.LOAD_CJK_PINYIN && w.getPinyin() != null ) {
								IWord wd = new Word(w.getPinyin(), IWord.T_CJK_PINYIN);
								wd.setPosition(w.getPosition());
								wordPool.add(wd);
							}
							//add the syn words to the poll
							if ( config.APPEND_CJK_SYN 
									&& config.LOAD_CJK_SYN && w.getSyn() != null ) {
								IWord wd;
								for ( int j = 0; j < w.getSyn().length; j++ ) {
									wd = new Word(w.getSyn()[j], w.getType());
									wd.setPartSpeech(w.getPartSpeech());
									wd.setPosition(w.getPosition());
									wordPool.add(wd);
								}
							}
							continue;
						}
					}
					
					IChunk chunk = getBestCJKChunk(chars, cjkidx);
					//System.out.println(chunk+"\n");
					//w = new Word(chunk.getWords()[0].getValue(), IWord.T_CJK_WORD);
					w = chunk.getWords()[0];
					
					/* 
					 * @istep 2: 
					 * 
					 * find the chinese name.
					 */
					int T = -1;
					if ( config.I_CN_NAME
							&& w.getLength() <= 2 && chunk.getWords().length > 1  ) {
						StringBuilder sb = new StringBuilder();
						sb.append(w.getValue());
						String str = null;
						
						//the w is a Chinese last name.
						if ( dic.match(ILexicon.CN_LNAME, w.getValue())
								&& (str = findCHName(chars, 0, chunk)) != null) {
							T = IWord.T_CN_NAME;
							sb.append(str);
						}
						//the w is Chinese last name adorn
						else if ( dic.match(ILexicon.CN_LNAME_ADORN, w.getValue())
								&& chunk.getWords()[1].getLength() <= 2
								&& dic.match(ILexicon.CN_LNAME, chunk.getWords()[1].getValue())) {
							T = IWord.T_CN_NICKNAME;
							sb.append(chunk.getWords()[1].getValue());
						}
						/*
						 * the length of the w is 2:
						 * the last name and the first char make up a word
						 * for the double name. 
						 */
						/*else if ( w.getLength() > 1
								&& findCHName( w, chunk ))  {
							T = IWord.T_CN_NAME;
							sb.append(chunk.getWords()[1].getValue().charAt(0));
						}*/
						
						if ( T != -1 ) {
							w = new Word(sb.toString(), T);
							//if ( config.APPEND_PART_OF_SPEECH )
							w.setPartSpeech(IWord.NAME_POSPEECH);
						}
					}
					
					//check the stopwords(clear it when Config.CLEAR_STOPWORD is true)
					if ( T == -1 && config.CLEAR_STOPWORD 
							&& dic.match(ILexicon.STOP_WORD, w.getValue()) ) {
						cjkidx += w.getLength();
						continue;
					}
					
					
					/*
					 * @istep 3:
					 * 
					 * reach the end of the chars - the last word.
					 * check the existence of the chinese and english mixed word
					 */
					IWord enAfter = null, ce = null;
					if ( cjkidx + w.getLength() >= chars.length && checkCE ) {
						//System.out.println("CE-Word"+w.getValue());
						enAfter = nextBasicLatin(readNext());
						//if ( enAfter.getType() == IWord.T_BASIC_LATIN ) {
						String cestr = w.getValue()+enAfter.getValue();
						
						/*
						 * here: (2013-08-31 added)
						 * also check the stopwords, and make sure
						 * 		the CE word is not a stop words.
						 */
						if ( ! (config.CLEAR_STOPWORD 
									&& dic.match(ILexicon.STOP_WORD, cestr))
								&& dic.match(ILexicon.CE_MIXED_WORD, cestr) ) {
							ce = dic.get(ILexicon.CE_MIXED_WORD, cestr);
							ce.setPosition(pos+cjkidx);
							wordPool.add(ce);
							cjkidx += w.getLength();
							enAfter = null;
						}
						//}
					}
					
					/*
					 * no ce word found, store the english word.
					 * 
					 * @reader: (2013-08-31 added)
					 * 	the newly found letter or digit word "enAfter" token 
					 * 	will be handled at last cause we have to handle 
					 * 			the pinyin and the syn words first.
					 */
					if ( ce == null ) {
						w.setPosition(pos+cjkidx);
						wordPool.add(w);
						cjkidx += w.getLength();
					} else {
						w = ce;
					}

					/*
					 * @istep 4:
					 * 
					 * handle the pinyin and the syn words.
					 */
					//add the pinyin to the pool
					if ( T == -1 && config.APPEND_CJK_PINYIN 
							&& config.LOAD_CJK_PINYIN && w.getPinyin() != null ) {
						IWord wd = new Word(w.getPinyin(), IWord.T_CJK_PINYIN);
						wd.setPosition(w.getPosition());
						wordPool.add(wd);
					}
					//add the syn words to the pool
					if ( T == -1 &&  config.APPEND_CJK_SYN 
							&& config.LOAD_CJK_SYN && w.getSyn() != null ) {
						IWord wd;
						String[] syns = w.getSyn();
						for ( int j = 0; j < syns.length; j++ ) {
							wd = new Word(syns[j], w.getType());
							wd.setPartSpeech(w.getPartSpeech());
							wd.setPosition(w.getPosition());
							wordPool.add(wd);
						}
					}
					
					//handle the after english word
					if ( enAfter != null && ! ( config.CLEAR_STOPWORD 
							&& dic.match(ILexicon.STOP_WORD, enAfter.getValue()) ) ) {
						enAfter.setPosition(chars.length);
						wordPool.add(enAfter);
					}
				}
				
				if ( wordPool.size() == 0 ) continue; 
				return wordPool.removeFirst();
			} 
			else if ( isEnChar(c) ) 
			{
				IWord w;
				if ( ENSCFilter.isEnPunctuation( c ) ) 
				{
					String str = ((char)c)+"";
					if ( config.CLEAR_STOPWORD 
							&& dic.match(ILexicon.STOP_WORD, str) ) continue;
					w = new Word(str, IWord.T_PUNCTUATION);
					w.setPartSpeech(IWord.PUNCTUATION);
				} else {
					w = nextBasicLatin(c);
					//clear the stopwords
					if ( config.CLEAR_STOPWORD 
							&& dic.match(ILexicon.STOP_WORD, w.getValue()) ) continue;
					
					/* @added: 2013-09-25
					 * append the english synoyms words.
					 */
					if ( config.APPEND_CJK_SYN 
							&& dic.match(ILexicon.EN_WORD, w.getValue()) ) {
						String[] syns = dic.get(ILexicon.EN_WORD, 
								w.getValue()).getSyn();
						if ( syns != null ) {
							IWord wd;
							for ( int j = 0;
									j < syns.length; j++ ) {
								wd = new Word(syns[j], w.getType());
								wd.setPartSpeech(w.getPartSpeech());
								wd.setPosition(w.getPosition());
								wordPool.add(wd);
							}
						}
					}
				}				
				w.setPosition(pos);
				return w;
			} 
			else if ( PPTFilter.isPairPunctuation( (char) c ) ) 
			{
				IWord w = null, w2 = null;
				String text = getPairPunctuationText(c);
				
				//handle the punctuation.
				String str = ((char)c)+"";
				if ( ! ( config.CLEAR_STOPWORD 
						&& dic.match(ILexicon.STOP_WORD, str) ) ) 
				{
					w = new Word(str, IWord.T_PUNCTUATION);
					w.setPartSpeech(IWord.PUNCTUATION);
					w.setPosition(pos);
				}
				
				//handle the pair text.
				if ( text != null && ! ( config.CLEAR_STOPWORD 
						&& dic.match(ILexicon.STOP_WORD, text) ) )
				{
					w2 = new Word( text, ILexicon.CJK_WORD );
					w2.setPartSpeech(IWord.PPT_POSPEECH);
					w2.setPosition(pos+1);
					
					if ( w == null ) w = w2;
					else wordPool.add(w2);
				}
				
				/* here: 
				 * 1. the punctuation is clear.
				 * 2. the pair text is null or being cleared.
				 * @date 2013-09-06
				 */
				if ( w == null && w2 == null ) continue;
				
				return w;
			} 
			else if ( isLetterNumber(c) ) 
			{
				IWord w = new Word(nextLetterNumber(c), IWord.T_OTHER_NUMBER);
				//clear the stopwords
				if ( config.CLEAR_STOPWORD 
						&& dic.match(ILexicon.STOP_WORD, w.getValue()) ) continue;
				w.setPartSpeech(IWord.NUMERIC_POSPEECH);
				w.setPosition(pos);
				return w;
			} 
			else if ( isOtherNumber(c) ) 
			{
				IWord w = new Word(nextOtherNumber(c), IWord.T_OTHER_NUMBER);
				//clear the stopwords
				if ( config.CLEAR_STOPWORD 
						&& dic.match(ILexicon.STOP_WORD, w.getValue()) ) continue;
				w.setPartSpeech(IWord.NUMERIC_POSPEECH);
				w.setPosition(pos);
				return w;
			} 
			else if ( ENSCFilter.isCnPunctuation( c ) ) 
			{
				String str = ((char)c)+"";
				if ( config.CLEAR_STOPWORD
						&& dic.match(ILexicon.STOP_WORD, str)) continue;
				IWord w = new Word(str, IWord.T_PUNCTUATION);
				w.setPartSpeech(IWord.PUNCTUATION);
				w.setPosition(pos);
				return w;
			} 
			
			/* @reader: (2013-09-25) 
			 * unrecognized char will cause unknow problem for different system.
			 * keep it or clear it ?
			 * if you use jcseg for search, better shut it down.
			 * */
			else if ( config.KEEP_UNREG_WORDS )
			{
				String str = ((char)c)+"";
				if ( config.CLEAR_STOPWORD
						&& dic.match(ILexicon.STOP_WORD, str)) continue;
				IWord w = new Word(str, IWord.T_UNRECOGNIZE_WORD);
				w.setPartSpeech(IWord.UNRECOGNIZE);
				w.setPosition(pos);
				return w;
			}
		}
		
		return null;
	}
	
	/**
	 * check the specified char is CJK,Thai... char
	 * 		true will be return if it is,
	 * 		or return false.
	 * 
	 * @param c
	 * @return boolean
	 */
	static boolean isCJKChar( int c ) {
		if ( Character.getType(c) == Character.OTHER_LETTER ) 
			return true;
		return false;
	}
	
	/**
	 * check the specified char is a basic latin and russia and greece letter
	 * 		true will be return if it is,
	 * 		or return false.<br />
	 * this method can recognize full-width char and letter.<br />
	 * 
	 * @param c
	 * @return boolean
	 */
	static boolean isEnChar( int c ) {
		/*int type = Character.getType(c);
		Character.UnicodeBlock cu = Character.UnicodeBlock.of(c);
		if ( ! Character.isWhitespace(c) && 
				(cu == Character.UnicodeBlock.BASIC_LATIN
				|| type == Character.DECIMAL_DIGIT_NUMBER
				|| type == Character.LOWERCASE_LETTER
				|| type == Character.UPPERCASE_LETTER
				|| type == Character.TITLECASE_LETTER
				|| type == Character.MODIFIER_LETTER)) 
			return true;
		return false;*/
		return ( ENSCFilter.isHWEnChar(c) || ENSCFilter.isFWEnChar(c) );
	}
	
	/**
	 * check the specified char is a digit or not.
	 * 		true will return if it is or return false
	 * this method can recognize full-with char.
	 * 
	 * @param	str
	 * @return	boolean
	 */
	static boolean isDigit( String str ) 
	{
		char c;
		for ( int j = 0; j < str.length(); j++ ) {
			c = str.charAt(j);
			//make full-width char half-width
			if ( c > 65280 ) c -= 65248;
			if ( c < 48 || c > 57 ) return false;
		}
		return true;
	}
	
	/**
	 * check the specified char is a decimal.
	 * 	including the full-width char.
	 * 
	 * @param	str
	 * @return	boolean
	 */
	static boolean isDecimal( String str ) 
	{
		if ( str.charAt(str.length() - 1) == '.' 
				|| str.charAt(0) == '.' ) return false;
		char c;
		int p= 0;		//number of point
		for ( int j = 1; j < str.length(); j++ ) {
			c = str.charAt(j);
			if ( c == '.' ) p++;
			else 
			{
				//make full-width half-width
				if ( c > 65280 ) c -= 65248;
				if ( c < 48 || c > 57 ) return false;
			}
		}
		
		return (p==1);
	}
	
	/**
	 * check the specified char is Letter number like 'ⅠⅡ'
	 * 		true will be return if it is,
	 * 		or return false. <br />
	 * 
	 * @param c
	 * @return boolean
	 */
	static boolean isLetterNumber( int c ) {
		if ( Character.getType(c) == Character.LETTER_NUMBER ) 
			return true;
		return false;
	}
	
	/**
	 * check the specified char is other number like '①⑩⑽㈩'
	 * 		true will be return if it is,
	 * 		or return false. <br />
	 * 
	 * @param c
	 * @return boolean
	 */
	static boolean isOtherNumber( int c ) {
		if ( Character.getType(c) == Character.OTHER_NUMBER ) 
			return true;
		return false;
	}
	
	/**
	 * match the next CJK word in the dictionary. <br />
	 * 
	 * @param chars
	 * @param index
	 * @return IWord[]
	 */
	protected IWord[] getNextMatch(char[] chars, int index) {
		
		ArrayList<IWord> mList = new ArrayList<IWord>(8);
		//StringBuilder isb = new StringBuilder();
		isb.clear();
	
		char c = chars[index];
		isb.append(c);
		String temp = isb.toString();
		if ( dic.match(ILexicon.CJK_WORD, temp) ) {
			mList.add(dic.get(ILexicon.CJK_WORD, temp));
		}
		
		String _key = null;
		for ( int j = 1; 
			j < config.MAX_LENGTH && ((j+index) < chars.length); j++ ) {
			isb.append(chars[j+index]);
			_key = isb.toString();
			if ( dic.match(ILexicon.CJK_WORD, _key) ) {
				mList.add(dic.get(ILexicon.CJK_WORD, _key));
			}
		}
		
		/*
		 * if match no words from the current position 
		 * to idx+Config.MAX_LENGTH, just return the Word with
		 * a value of temp as a unrecognited word. 
		 */
		if ( mList.isEmpty() ) {
			mList.add(new Word(temp, ILexicon.UNMATCH_CJK_WORD));
		}
		
/*		for ( int j = 0; j < mList.size(); j++ ) {
			System.out.println(mList.get(j));
		}*/
		
		IWord[] words = new IWord[mList.size()];
		mList.toArray(words);
		mList.clear();
		
		return words;
	}
	
	/**
	 * find the chinese name from the position of the given word.
	 * 
	 * @param chars
	 * @param index
	 * @param chunk
	 * @return IWord
	 */
	protected String findCHName( char[] chars, int index, IChunk chunk ) {
		StringBuilder isb = new StringBuilder();
		//isb.clear();
		/*there is only two IWords in the chunk. */
		if ( chunk.getWords().length == 2 ) {
			IWord w = chunk.getWords()[1];
			switch ( w.getLength() ) {
			case 1:
				if ( dic.match(ILexicon.CN_SNAME, w.getValue()) ) {
					isb.append(w.getValue());
					return isb.toString();
				}
				return null;
			case 2:
			case 3:
				/*
				 * there is only two IWords in the chunk.
				 * case 2:
				 * like: 这本书是陈高的, chunk: 陈_高的
				 * more: 瓜子和坚果,chunk: 和_坚果 (1.6.8前版本有歧义)
				 * case 3:
				 * 1.double name: the two chars and char after it make up a word.
				 * like: 这本书是陈美丽的, chunk: 陈_美丽的
				 * 2.single name: the char and the two chars after it make up a word. -ignore
				 */
				String d1 = new String(w.getValue().charAt(0)+"");
				String d2 = new String(w.getValue().charAt(1)+"");
				if ( dic.match(ILexicon.CN_DNAME_1, d1)
						&& dic.match(ILexicon.CN_DNAME_2, d2)) {
					isb.append(d1);
					isb.append(d2);
					return isb.toString();
				} 
				/*
				 * the name char of the single name and the char after it
				 * 		make up a word. 
				 */
				else if ( dic.match(ILexicon.CN_SNAME, d1) ) {
					IWord iw = dic.get(ILexicon.CJK_WORD, d2);
					if ( iw != null && iw.getFrequency() >= config.NAME_SINGLE_THRESHOLD ) {
						isb.append(d1);
						return isb.toString();
					}
				}
				return null;
			}
		}
		/*three IWords in the chunk */
		else {
			IWord w1 = chunk.getWords()[1];
			IWord w2 = chunk.getWords()[2];
			switch ( w1.getLength() ) {
			case 1:
				/*check if it is a double name first.*/
				if ( dic.match(ILexicon.CN_DNAME_1, w1.getValue()) ) {
					if ( w2.getLength() == 1 ) {
						/*real double name?*/
						if ( dic.match(ILexicon.CN_DNAME_2, w2.getValue()) ) {
							isb.append(w1.getValue());
							isb.append(w2.getValue());
							return isb.toString();
						}
						/*not a real double name, check if it is a single name.*/
						else if ( dic.match(ILexicon.CN_SNAME, w1.getValue()) ) {
							isb.append(w1.getValue());
							return isb.toString();
						}
					} 
					/*
					 * double name:
					 * char 2 and the char after it make up a word.
					 * like: 陈志高兴奋极了, chunk:陈_志_高兴 (兴和后面成词)
					 * like: 陈志高的, chunk:陈_志_高的 ("的"的阕值Config.SINGLE_THRESHOLD)
					 * like: 陈高兴奋极了, chunk:陈_高_兴奋 (single name)
					 */
					else {
						String d1 = new String(w2.getValue().charAt(0)+"");
						int index_ = index + chunk.getWords()[0].getLength() + 2;
						IWord[] ws = getNextMatch(chars, index_);
						//System.out.println("index:"+index+":"+chars[index]+", "+ws[0]);
						/*is it a double name?*/
						if ( dic.match(ILexicon.CN_DNAME_2, d1) && 
								(ws.length > 1 || ws[0].getFrequency() >= config.NAME_SINGLE_THRESHOLD)) {
							isb.append(w1.getValue());
							isb.append(d1);
							return isb.toString();
						}
						/*check if it is a single name*/
						else if ( dic.match(ILexicon.CN_SNAME, w1.getValue()) ) {
							isb.append(w1.getValue());
							return isb.toString();
						}
					}
				} 
				/*check if it is a single name.*/
				else if ( dic.match(ILexicon.CN_SNAME, w1.getValue()) ) {
					isb.append(w1.getValue());
					return isb.toString();
				}
				return null;
			case 2:
				String d1 = new String(w1.getValue().charAt(0)+"");
				String d2 = new String(w1.getValue().charAt(1)+"");
				/*
				 * it is a double name and char 1, char 2 make up a word.
				 * like: 陈美丽是对的, chunk: 陈_美丽_是
				 * more: 都成为高速公路, chunk:都_成为_高速公路 (1.6.8以前的有歧义)
				 */
				if ( dic.match(ILexicon.CN_DNAME_1, d1)
						&& dic.match(ILexicon.CN_DNAME_2, d2)) {
					isb.append(w1.getValue());
					return isb.toString();
				}
				/*
				 * it is a single name, char 1 and the char after it make up a word.
				 */
				else if ( dic.match(ILexicon.CN_SNAME, d1) ) {
					IWord iw = dic.get(ILexicon.CJK_WORD, d2);
					if ( iw != null && iw.getFrequency() >= config.NAME_SINGLE_THRESHOLD ) {
						isb.append(d1);
						return isb.toString();
					}
				}
				return null;
			case 3:
				/*
				 * singe name:  - ignore
				 *  mean the char and the two chars after it make up a word.
				 *  
				 * it is a double name.
				 * like: 陈美丽的人生， chunk: 陈_美丽的_人生
				 */
				String c1 = new String(w1.getValue().charAt(0)+"");
				String c2 = new String(w1.getValue().charAt(1)+"");
				IWord w3 = dic.get(ILexicon.CJK_WORD, w1.getValue().charAt(2)+"");
				if ( dic.match(ILexicon.CN_DNAME_1, c1)
						&& dic.match(ILexicon.CN_DNAME_2, c2)
						&& (w3 == null || w3.getFrequency() >= config.NAME_SINGLE_THRESHOLD)) {
					isb.append(c1);
					isb.append(c2);
					return isb.toString();
				}
				return null;
			}
		}
		return null;
	}
	
	/**
	 * find the Chinese double name:
	 * when the last name and the first char of the name make up a word.
	 * 
	 * @param chunk the best chunk.
	 * @return boolean
	 */
	@Deprecated
	public boolean findCHName( IWord w, IChunk chunk ) {
		String s1 = new String(w.getValue().charAt(0)+"");
		String s2 = new String(w.getValue().charAt(1)+"");
		
		if ( dic.match(ILexicon.CN_LNAME, s1)
				&& dic.match(ILexicon.CN_DNAME_1, s2)) {
			IWord sec = chunk.getWords()[1]; 
			switch ( sec.getLength() ) {
			case 1:
				if ( dic.match(ILexicon.CN_DNAME_2, sec.getValue()) )
					return true;
			case 2:
				String d1 = new String(sec.getValue().charAt(0)+"");
				IWord _w = dic.get(ILexicon.CJK_WORD, sec.getValue().charAt(1)+"");
				//System.out.println(_w);
				if ( dic.match(ILexicon.CN_DNAME_2, d1)
						&& (_w == null 
						|| _w.getFrequency() >= config.NAME_SINGLE_THRESHOLD ) ) 
					return true;
			}
		}
		
		return false;
	}
	
	/**
	 * load a CJK char list from the stream start from the current position.
	 * 		till the char is not a CJK char.<br />
	 * 
	 * @param c
	 * @return char[]
	 * @throws IOException
	 */
	protected char[] nextCJKSentence( int c ) throws IOException {
		//StringBuilder isb = new StringBuilder();
		isb.clear();
		int ch;
		isb.append((char)c);
		
		checkCE = false;
		while ( (ch = readNext()) != -1 ) {
			if ( ENSCFilter.isWhitespace(ch) ) break;
			if ( ! isCJKChar(ch) ) {
				pushBack(ch);
				/*check chinese english mixed word*/
				if ( ENSCFilter.isEnLetter(ch) ) checkCE = true;
				break;
			} 
			isb.append((char)ch);
		}
		return isb.toString().toCharArray();
	}
	
	/**
	 * find the letter or digit word from the current position.<br />
	 * 		count until the char is whitespace or not letter_digit. 
	 * 
	 * @param c
	 * @return IWord
	 * @throws IOException 
	 */
	protected IWord nextBasicLatin( int c ) throws IOException {
		//StringBuilder isb = new StringBuilder();
		isb.clear();
		if ( ENSCFilter.isFWEnChar(c) ) c = c - 65248;
		if ( ENSCFilter.isUpperCaseLetter(c) ) c = c + 32; 
		isb.append((char)c);
		int ch;
		boolean __check = false, __wspace = false;
		while ( (ch = readNext()) != -1 ) 
		{
			if ( ENSCFilter.isWhitespace(ch) ) {
				__wspace = true;
				break;
			}
			if ( ENSCFilter.isEnPunctuation(ch) 
					&& ! ENSCFilter.isENKeepChar((char)ch) ) {
				pushBack(ch);
				break;
			} 
			if ( ! isEnChar(ch) ) {
				pushBack(ch);
				if ( isCJKChar( ch ) ) __check = true;
				break;
			}
			
			//turn the full-width char to half-width char.
			if ( ENSCFilter.isFWEnChar(ch) )
				ch = ch - 65248;
			//turn the lower case letter to upper case.
			if ( ENSCFilter.isUpperCaseLetter(ch) )
				ch = ch + 32;

			isb.append((char)ch);
		}
		
		String __str = isb.toString();
		IWord w = null;
		boolean chkunits = true;
		
		/* 
		 * @step 2: 
		 * 1. delete the useless english punctuations.
		 * 2. try to find the english and punctuation mixed word. 
		 */
		for ( int t = isb.length() - 1; t > 0 
					&& isb.charAt(t) != '%'
					&& ENSCFilter.isEnPunctuation(isb.charAt(t)) ; t-- ) {
			/*
			 * try to find a english and punctuation mixed word.
			 * 	this will clear all the punctuation until a mixed word is found.
			 * like "i love c++.", c++ will be found from token "c++.".
			 * @date 2013-08-31 
			 */
			if ( dic.match(ILexicon.EN_PUN_WORD, __str) ) {
				w = dic.get(ILexicon.EN_PUN_WORD, __str);
				w.setPartSpeech(IWord.EN_POSPEECH);
				chkunits = false;
				//return w;
				break;
			}
			
			/*
			 * keep the en punctuation.
			 * @date 2013-09-06 
			 */
			pushBack(isb.charAt(t));
			isb.deleteCharAt(t);
			__str = isb.toString();
		}
		
		//@step 3: check the end condition.
		// and the check if the token loop was break by whitespace
		//cause there is no need to continue all the following work if it is.
		//@added 2013-11-19
		if ( ch == -1 || __wspace ) {
			w = new Word(__str, IWord.T_BASIC_LATIN);
			w.setPartSpeech(IWord.EN_POSPEECH);
			return w;
		}
		
		if ( ! __check )  
		{	
			/* @reader: (2013-09-25)
			 * we check the units here, so we can recognize
			 * many other units that is not chinese like '℉,℃' eg..
			 * */
			if ( chkunits 
					&& ( isDigit(__str) || isDecimal(__str) ) ) {
				ch = readNext();
				if ( dic.match(ILexicon.CJK_UNITS, ((char)ch)+"") ) {
					w = new Word(new String(__str+((char)ch)), IWord.T_MIXED_WORD);
					w.setPartSpeech(IWord.NUMERIC_POSPEECH);
				} 
				else pushBack(ch);
			}
			
			if ( w == null ) {
				w = new Word(__str, IWord.T_BASIC_LATIN);
				w.setPartSpeech(IWord.EN_POSPEECH);
			}
			
			return w;
		}
		
		
		//@step 4: check and get english and chinese mix word like 'B超'.
		StringBuilder mixWord = new StringBuilder();
		mixWord.append(__str);
		String _temp = null;
		int mc = 0, j = 0;		//the number of char that readed from the stream.
		
		//replace width IntArrayList at 2013-09-08
		//ArrayList<Integer> chArr = new ArrayList<Integer>(config.MIX_CN_LENGTH);
		ialist.clear();
		
		/* Attension:
		 * make sure that (ch = readNext()) is after j < Config.MIX_CN_LENGTH.
		 * or it cause the miss of the next char. 
		 * 
		 * @reader: (2013-09-25)
		 * we do not check the type of the char readed next.
		 * so, words started with english and its length except the start english part
		 * less than config.MIX_CN_LENGTH in the EC dictionary could be recongnized.
		 */
		for ( ; j < config.MIX_CN_LENGTH 
					&& (ch = readNext()) != -1; j++ ) 
		{
			/* Attension:
			 *  it is a chance that jcseg works find for 
			 *  	we break the loop directly when we meet a whitespace.
			 *  1. if a EC word is found, unit check process will be ignore.
			 *  2. if matches no EC word, certianly return of readNext() 
			 *  	will make sure the units check process works find.
			 */
			if ( ENSCFilter.isWhitespace(ch) ) break; 
			mixWord.append((char)ch);
			//System.out.print((char)ch+",");
			ialist.add(ch);
			_temp = mixWord.toString();
			//System.out.println((j+1)+": "+_temp);
			if ( dic.match(ILexicon.EC_MIXED_WORD, _temp) ) {
				w = dic.get(ILexicon.EC_MIXED_WORD, _temp);
				mc = j + 1;
			}
		}
		
		//push back the readed chars.
		for ( int i = j - 1; i >= mc; i-- ) 
			pushBack(ialist.get(i));
		//chArr.clear();chArr = null;
		
		/* @step 5: check if there is a units for the digit.
		 * @reader: (2013-09-25)
		 * now we check the units before the step 4, so we can recognize
		 * many other units that is not chinese like '℉,℃'
		 * */
		if ( chkunits && mc == 0 ) {
			if ( isDigit(__str) || isDecimal(__str) ) {
				ch = readNext();
				if ( dic.match(ILexicon.CJK_UNITS, ((char)ch)+"") ) {
					w = new Word(new String(__str+((char)ch)), IWord.T_MIXED_WORD);
					w.setPartSpeech(IWord.NUMERIC_POSPEECH);
				} else pushBack(ch);
			}
		}
		
		if ( w == null ) {
			w = new Word(__str, IWord.T_BASIC_LATIN);
			w.setPartSpeech(IWord.EN_POSPEECH);
		}
		
		return w;
	}
	
	/**
	 * find the next other letter from the current position.
	 * 		find the letter number from the current position.
	 * 		count until the char in the specified position is not 
	 * 		a letter number or whitespace. <br />
	 * 
	 * @param c
	 * @return String
	 * @throws IOException
	 */
	protected String nextLetterNumber( int c ) throws IOException {
		//StringBuilder isb = new StringBuilder();
		isb.clear();
		isb.append((char)c);
		int ch;
		while ( (ch = readNext()) != -1 ) {
			if ( ENSCFilter.isWhitespace(ch) ) break;
			if ( ! isLetterNumber( ch ) ) {
				pushBack(ch);
				break;
			} 
			isb.append((char)ch);
		}
		
		return isb.toString();
	}
	
	/**
	 * find the other number from the current position. <br />
	 * 		count until the char in the specified position is not
	 * 		a orther number or whitespace. <br />
	 * 
	 * @param c
	 * @return String
	 * @throws IOException
	 */
	protected String nextOtherNumber( int c ) throws IOException {
		//StringBuilder isb = new StringBuilder();
		isb.clear();
		isb.append((char)c);
		int ch;
		while ( (ch = readNext()) != -1 ) {
			if ( ENSCFilter.isWhitespace(ch) ) break;
			if ( ! isOtherNumber(ch) ) {
				pushBack(ch);
				break;
			} 
			isb.append((char)ch);
		}
		
		return isb.toString();
	}
	
	/**
	 * find the chinese number from the current position. <br />
	 * 		count until the char in the specified position is not
	 * 		a orther number or whitespace. <br />
	 * 
	 * @param chars char array of CJK items.
	 * @param index
	 * @return String[]
	 */
	protected String nextCNNumeric( char[] chars, int index ) throws IOException {
		//StringBuilder isb = new StringBuilder();
		isb.clear();
		isb.append( chars[ index ]);
		for ( int j = index + 1; j < chars.length; j++ ) {
			//System.out.println("cn:"+chars[j]);
			if ( CNNMFilter.isCNNumeric( chars[j] ) == -1 ) {
				//deal with “分之”
				if ( j + 2 < chars.length
						&& chars[j] == '分' && chars[j+1] == '之') {
					isb.append(chars[j++]);
					isb.append(chars[j]);
					continue;
				} 
				else 
					break;
			}
			isb.append( chars[j] );
		}
		return isb.toString();
	}
	
	/**
	 * find pair punctuation of the given punctuation char.
	 * the purpose is to get the text bettween them. <br />
	 * 
	 * @param c
	 * @throws IOException 
	 */
	protected String getPairPunctuationText( int c ) throws IOException {
		//StringBuilder isb = new StringBuilder();
		isb.clear();
		char echar = PPTFilter.getPunctuationPair( (char) c);
		boolean matched = false;
		int j, ch;
		
		//replaced with IntArrayList at 2013-09-08
		//ArrayList<Integer> chArr = new ArrayList<Integer>(config.PPT_MAX_LENGTH);
		ialist.clear();
		
		for ( j = 0; j < config.PPT_MAX_LENGTH; j++ ) 
		{
			ch = readNext();
			if ( ch == -1 ) break;
			if ( ch == echar ) {
				matched = true;
				pushBack(ch);		//push the pair punc back.
				break;
			}
			isb.append( (char) ch );
			ialist.add(ch);
		}
		
		if ( matched == false ) {
			for ( int i = j - 1; i >= 0; i-- ) 
				pushBack( ialist.get(i) );
			return null;
		}
		
		return isb.toString();
	}
	
	/**
	 * an abstract method to gain a CJK word from the 
	 * current position.
	 * 		simpleSeg and ComplexSeg is different to deal this,
	 * 		so make it a abstract method here.
	 * 
	 * @param  chars
	 * @param  index
	 * @return IChunk
	 * @throws IOException
	 */
	protected abstract IChunk getBestCJKChunk(char chars[], int index) throws IOException;

}
