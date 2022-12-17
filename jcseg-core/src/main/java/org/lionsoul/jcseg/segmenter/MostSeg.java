package org.lionsoul.jcseg.segmenter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import org.lionsoul.jcseg.IWord;
import org.lionsoul.jcseg.dic.ADictionary;
import org.lionsoul.jcseg.dic.ILexicon;
import org.lionsoul.jcseg.util.IStringBuffer;

/**
 * Most mode implementation which all the possible combinations will be returned, 
 * and build it for information retrieval (better for index) of course.
 * 
 * @author  chenxin<chenxin619315@gmail.com>
 * @since   1.9.8
*/
public class MostSeg extends Segmenter
{
    
    public MostSeg(SegmenterConfig config, ADictionary dic) 
    {
        super(config, dic);
        config.setKeepEnSecOriginalWord(true);
    }

    /**
     * get the next CJK word from the current position of the input stream
     * and this function is the core part the most segmentation implements
     * 
     * @see Segmenter#getNextCJKWord(int, int)
    */
    @Override 
    protected IWord getNextCJKWord(int c, int pos) throws IOException
    {
        String key = null;
        char[] chars = nextCJKSentence(c);
        int cjkidx = 0, ignidx = 0, mnum = 0;
        IWord word = null;
        final ArrayList<IWord> mList = new ArrayList<>(8);
        
        while ( cjkidx < chars.length ) {
            /// @Note added at 2017/04/29
            /// check and append the single char word
            String sstr = String.valueOf(chars[cjkidx]);
            if ( dic.match(ILexicon.CJK_WORD, sstr) ) {
                IWord sWord = dic.get(ILexicon.CJK_WORD, sstr).clone();
                sWord.setPosition(pos+cjkidx);
                mList.add(sWord);
            }
            
            mnum = 0;
            isb.clear().append(chars[cjkidx]);
            //System.out.println("ignore idx: " + ignidx);
            for ( int j = 1; j < config.MAX_LENGTH && (cjkidx+j) < chars.length; j++ ) {
                isb.append(chars[cjkidx+j]);
                key = isb.toString();
                if ( dic.match(ILexicon.CJK_WORD, key) ) {
                    mnum   = 1;
                    ignidx = Math.max(ignidx, cjkidx + j);
                    word = dic.get(ILexicon.CJK_WORD, key).clone();
                    word.setPosition(pos+cjkidx);
                    mList.add(word);
                }
            }
            
            /*
             * no matches here:
             * should the current character chars[cjkidx] be a single word ?
             * lets do the current check 
            */
            if ( mnum == 0 && (cjkidx == 0 || cjkidx > ignidx) ) {
                String temp = String.valueOf(chars[cjkidx]);
                if ( ! dic.match(ILexicon.CJK_WORD, temp) ) {
                    word = new Word(temp, ILexicon.UNMATCH_CJK_WORD);
                    word.setPartSpeech(IWord.UNRECOGNIZE);
                    word.setPosition(pos+cjkidx);
                    mList.add(word);
                }
            }
            
            cjkidx++;
        }
        
        /*
         * do all the words analysis
         * 1, clear the stop words
         * 1, check and append the pinyin or synonyms words
        */
        for ( IWord w : mList ) {
            key = w.getValue();
            if ( config.CLEAR_STOPWORD 
                    && dic.match(ILexicon.STOP_WORD, key) ) {
                continue;
            }
            
            wordPool.add(w);
            appendCJKWordFeatures(w);
        }
        
        //let gc do its work
        mList.clear();

        return wordPool.size()==0 ? null : wordPool.remove();
    }
    
    /**
     * @see Segmenter#enSecondSegFilter(IWord) 
    */
    protected boolean enSecondSegFilter(IWord w)
    {
    	return true;
    }
    
    /**
     * Latin word lexicon based English word segmentation for search mode
     * 
     * @param	w
     * @param	wList
     * @return	LinkedList<IWord> all the sub word tokens
    */
    @Override
    protected LinkedList<IWord> enWordSeg(IWord w, LinkedList<IWord> wList)
    {
    	int curidx = 0, pos = w.getPosition(), len = w.getValue().length();
    	IWord tw = null;
    	String str = null;
    	int ignidx = 0, mnum = 0;
    	boolean ignore = false;

    	/* for search module we track the whole original token as one of the result */
    	wList.add(w);
    	
    	final IStringBuffer sb = new IStringBuffer(config.EN_MAX_LEN);
    	final char[] chars = w.getValue().toCharArray();
    	while ( curidx < chars.length ) {
    		/* check and append the single letter word */
			str = String.valueOf(chars[curidx]);
			ignore = (curidx == 0 && str.length() == len);
			if ( !ignore && dic.match(ILexicon.CJK_WORD, str) ) {
				tw = dic.get(ILexicon.CJK_WORD, str).clone();
				tw.setPosition(pos+curidx);
                wList.add(tw);
			}
    		
    		sb.clear().append(chars[curidx]);
    		for ( int j = 1; j < config.EN_MAX_LEN && (curidx+j) < chars.length; j++ ) {
    			sb.append(chars[curidx+j]);
    			str = sb.toString();
    			
    			/* check and ignore the source w itself */
    			if ( curidx == 0 && str.length() == len ) {
    				continue;
    			}
    			
    			if ( dic.match(ILexicon.CJK_WORD, str) ) {
    				mnum = 1;
    				ignidx = Math.max(ignidx, curidx + j);
    				tw = dic.get(ILexicon.CJK_WORD, str).clone();
    				tw.setPosition(pos+curidx);
    				wList.add(tw);
    			}
    		}
    		
    		/*
             * no matches here:
             * should the current character chars[cjkidx] be a single word ?
             * let's do the current check
            */
            if ( !ignore && mnum == 0 && (curidx == 0 || curidx > ignidx) ) {
                String temp = String.valueOf(chars[curidx]);
                if ( ! dic.match(ILexicon.CJK_WORD, temp) ) {
                    tw = new Word(temp, ILexicon.UNMATCH_CJK_WORD);
                    tw.setPartSpeech(IWord.UNRECOGNIZE);
                    tw.setPosition(pos+curidx);
                    wList.add(tw);
                }
            }
            
    		curidx++;
    	}
    	
    	sb.clear();
    	return wList;
    }
    
}
