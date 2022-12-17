package org.lionsoul.jcseg.segmenter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;

import org.lionsoul.jcseg.ISegment;
import org.lionsoul.jcseg.IWord;
import org.lionsoul.jcseg.dic.ADictionary;
import org.lionsoul.jcseg.dic.ILexicon;
import org.lionsoul.jcseg.fi.CharTypeFunction;
import org.lionsoul.jcseg.util.IPushbackReader;
import org.lionsoul.jcseg.util.IStringBuffer;
import org.lionsoul.jcseg.util.StringUtil;

/**
 * Jcseg n-gram tokenizer implementation
 * 
 * @author lionsoul<chenxin619315@gmail.com>
 * @since  2.6.0
*/

public class NGramSeg implements ISegment
{
    /**
     * the index value of the current input stream
     * mainly for track the start position of the token
    */
    protected int idx;
    protected IPushbackReader reader = null;
    
    /**
     * CJK word cache pool, Reusable string buffer
    */
    final protected LinkedList<IWord> wordPool;
    final protected IStringBuffer isb;
    
    /**
     * the dictionary and task configuration
    */
    public final ADictionary dic;
    public final SegmenterConfig config;
    
    /** The N for n-gram, default to 1 and that is uni-gram */
    protected byte N = 1;
     
    
    /**
     * method to create a new ISegment
     * 
     * @param   config
     * @param   dic
     */
    public NGramSeg(SegmenterConfig config, ADictionary dic)
    {
    	assert config.getGRAM() > 0;
        this.config = config;
        this.dic    = dic;
        this.N 		= config.getGRAM();
        wordPool    = new LinkedList<>();
        isb         = new IStringBuffer(N + 1);
    }

    @Override
    public void reset(Reader input) throws IOException
    {
        if ( input != null ) {
            reader = new IPushbackReader(new BufferedReader(input));
        }
        
        idx = -1;
    }

    @Override
    public int getStreamPosition()
    {
        return idx + 1;
    }
    
    /**
     * read the next char from the current position
     * 
     * @return  int
     */
    protected int readNext() throws IOException 
    {    
        int c = reader.read();
        if ( c != -1 ) idx++;
        return c;
    }
    
    /**
     * push back the data to the stream
     * 
     * @param   data
     */
    protected void pushBack( int data )
    {
        reader.unread(data);
        idx--;
    }
    
    /** reset the data back from the specified position */
    protected void streamResetTo(String str, int start)
    {
		for ( int i = start; i < str.length(); i++ ) {
			pushBack(str.charAt(i));
		}
    }

    @Override
    public IWord next() throws IOException
    {
        /*
         * @Note: check and get the token directly from the word pool
         * if word pool is available.
         */
        if ( wordPool.size() > 0 ) {
            return wordPool.remove();
        }
        
        int c, pos, type;
        CharTypeFunction checker = null;
        String[] pofs = null;
        
        IWord word = null;
        while ( (c = readNext()) != -1 ) {
            if ( StringUtil.isWhitespace(c) ) {
                continue;
            }
            
            pos  = idx;
            type = 0;
            checker = null;
            pofs = null;
            
            /* Chinese and Latin punctuation. */
            if ( StringUtil.isPunctuation(c) ) {
            	String str = String.valueOf((char)c);
                if ( config.CLEAR_STOPWORD
                        && dic.match(ILexicon.STOP_WORD, str)) {
                    continue;
                }
                
                word = new Word(str, IWord.T_PUNCTUATION);
                word.setPartSpeechForNull(IWord.PUNCTUATION);
                word.setPosition(pos);
                return word;
            }
            
            /* CJK char */
            if ( StringUtil.isCJKChar( c ) ) {
            	type = IWord.T_CJK_WORD;
            	checker = StringUtil::isCJKChar;
            }
            /* English/Latin char. */
            else if ( StringUtil.isEnLetter(c) ) {
            	type = IWord.T_BASIC_LATIN;
            	checker = StringUtil::isEnLetter;
            	pofs = IWord.EN_POSPEECH;
            	/* full-width and uppercase conversion */
                if ( c > 65280 ) c -= 65248;
                if ( c >= 65 && c <= 90 ) c += 32; 
            }
            /* letter number like 'ⅠⅡ' */
            else if ( StringUtil.isLetterNumber(c) ) {
            	type = IWord.T_LETTER_NUMBER;
            	checker = StringUtil::isLetterNumber;
            	pofs = IWord.NUMERIC_POSPEECH;
            }
            /* other number like '①⑩⑽㈩' */
            else if ( StringUtil.isOtherNumber(c) ) {
            	type = IWord.T_OTHER_NUMBER;
            	checker = StringUtil::isOtherNumber;
            	pofs = IWord.NUMERIC_POSPEECH;
            }
            /* unrecognized char will cause unknown problem for different system.
             * keep it or clear it ?
             * if you use Jcseg for search, better close it. */
            else if ( config.KEEP_UNREG_WORDS ) {
            	String str = String.valueOf((char)c);
                if ( config.CLEAR_STOPWORD
                        && dic.match(ILexicon.STOP_WORD, str)) {
                    continue;
                }
                
                word = new Word(str, IWord.T_UNRECOGNIZE_WORD);
                word.setPartSpeechForNull(IWord.UNRECOGNIZE);
                word.setPosition(pos);
                return word;
            }
            
            final String str = (N == 1) ? String.valueOf((char)c) : getNextType(c, type, checker);
            /* reach the end of the stream ?*/
			/// if ( str == null ) { continue; }

    		if ( config.CLEAR_STOPWORD && dic.match(ILexicon.STOP_WORD, str)) {
    			if ( N > 1 ) {
    				streamResetTo(str, 1);
    			}
                continue;
            }
            
    		boolean append_syn = (config.LOAD_CJK_SYN && config.APPEND_CJK_SYN);
    		boolean append_pinyin = (config.LOAD_CJK_PINYIN && config.APPEND_CJK_PINYIN);
    		if ( append_syn || append_pinyin ) {
    			word = wordNewOrClone(ILexicon.CJK_WORD, str, type);
    		} else {
    			word = new Word(str, type);
    		}
    		word.setPosition(pos);
    		word.setPartSpeechForNull(pofs);
    		
    		/* check and append the word features like synonyms/pinyin words */
    		if ( append_syn && word.getSyn() != null ) {
    			SegKit.appendSynonyms(config, wordPool, word);
    		}
    		
    		if ( append_pinyin && word.getPinyin() != null ) {
    			SegKit.appendPinyin(config, wordPool, word);
    		}
    		
    		if ( N > 1 ) {
				streamResetTo(str, 1);
			}
    		return word;
        }
        
        return null;
    }
    
    /**
     * common interface to get the next n-gram word for the specified char type.
     * For the basic Latin char this will automatically do the full-width to half-width
     * uppercase to lowercase conversion.
     * 
     * @param	c
     * @param	type
     * @param	checker
     * @return	IWord
    */
    protected String getNextType(int c, int type, CharTypeFunction checker) throws IOException
    {
    	isb.clear().append((char)c);
    	int ch;
    	while ( (ch = readNext()) != -1 ) {
    		if ( type == IWord.T_BASIC_LATIN ) {
                if ( ch > 65280 ) ch -= 65248;
                if ( ch >= 65 && ch <= 90 ) ch += 32; 
        	}
    		
    		if ( StringUtil.isWhitespace(ch) ) {
                pushBack(ch);
                break;
            }
    		
    		if ( ! checker.is(ch) ) {
                pushBack(ch);
                break;
            }
    		
    		isb.append((char)ch);
    		if ( isb.length() >= N ) {
    			break;
    		}
    	}
    	
    	return isb.toString();
    }
    
    /**
     * check if the specified word is existed in a specified dictionary
     * and if does clone it or create a new one.
     * 
     * @param	t
     * @param	str
     * @param	type
    */
    public IWord wordNewOrClone(int t, String str, int type)
    {
    	return dic.match(t, str) ? dic.get(t, str).clone() : new Word(str, type);
    }

	public ADictionary getDic() {
		return dic;
	}

	public SegmenterConfig getConfig() {
		return config;
	}

	public byte getN() {
		return N;
	}

	public void setN(byte n) {
		assert n > 0;
		N = n;
	}

}
