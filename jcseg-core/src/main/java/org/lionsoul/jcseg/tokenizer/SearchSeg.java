package org.lionsoul.jcseg.tokenizer;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.IChunk;
import org.lionsoul.jcseg.tokenizer.core.ILexicon;
import org.lionsoul.jcseg.tokenizer.core.IWord;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;

/**
 * search mode implementation all the possible combination will be returned, 
 * and build it for search of course.
 * 
 * @author  chenxin<chenxin619315@gmail.com>
 * @since   1.9.8
*/
public class SearchSeg extends ASegment
{
    
    public SearchSeg(JcsegTaskConfig config, ADictionary dic) throws IOException
    {
        super(config, dic);
    }
    
    public SearchSeg(Reader input, JcsegTaskConfig config, ADictionary dic) throws IOException
    {
        super(input, config, dic);
    }

    /**
     * get the next CJK word from the current position of the input stream
     * and this function is the core part the most segmentation implements
     * 
     * @see ASegment#getNextCJKWord(int, int)
     * @throws IOException 
    */
    @Override protected IWord getNextCJKWord(int c, int pos) throws IOException
    {
        String key = null;
        char[] chars = nextCJKSentence(c);
        int cjkidx = 0, ignidx = 0, mnum = 0;
        IWord word = null;
        ArrayList<IWord> mList = new ArrayList<IWord>(8);
        
        while ( cjkidx < chars.length ) {
            mnum = 0;
            isb.clear().append(chars[cjkidx]);
            //System.out.println("ignore idx: " + ignidx);
            for ( int j = 1; j < config.MAX_LENGTH 
                    && (cjkidx+j) < chars.length; j++ ) {
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
                if ( dic.match(ILexicon.CJK_WORD, temp) == false ) {
                    word = new Word(temp, ILexicon.UNMATCH_CJK_WORD);
                    word.setPosition(pos+cjkidx);
                    mList.add(new Word(temp, ILexicon.UNMATCH_CJK_WORD));
                } else {
                    word = dic.get(ILexicon.CJK_WORD, temp).clone();
                    word.setPosition(pos+cjkidx);
                    mList.add(word);
                    appendWordFeatures(word);
                }
            }
            
            cjkidx++;
            if ( cjkidx > chars.length ) {
                break;
            }
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
            appendWordFeatures(w);
        }
        
        //let gc do its work
        mList.clear();
        mList = null;
        
        return wordPool.size()==0 ? null : wordPool.remove();
    }

    /**
     * here we don't have to do anything
     * 
     * @see ASegment#getBestCJKChunk(char[], int)
    */
    @Override
    protected IChunk getBestCJKChunk(char[] chars, int index) throws IOException
    {
        return null;
    }
    
}
