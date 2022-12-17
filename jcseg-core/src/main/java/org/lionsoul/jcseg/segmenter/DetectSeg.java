package org.lionsoul.jcseg.segmenter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.util.LinkedList;

import org.lionsoul.jcseg.util.StringUtil;
import org.lionsoul.jcseg.ISegment;
import org.lionsoul.jcseg.IWord;
import org.lionsoul.jcseg.dic.ADictionary;
import org.lionsoul.jcseg.dic.ILexicon;
import org.lionsoul.jcseg.util.IPushbackReader;
import org.lionsoul.jcseg.util.IStringBuffer;

/**
 * <p>
 * Detect segmentation mode return words only in the loaded dictionary
 * yat, when matched a word and return it
 * or continue to find the next word in the dictionary
 * </p>
 * 
 * @author  chenxin <chenxin619315@gmail.com>
 * @since   1.9.4
*/
public class DetectSeg implements ISegment, Serializable
{
    private static final long serialVersionUID = 1L;
    /**
     * the index of the current input stream 
    */
    private int idx;
    
    /**
     * runtime needed push back reader and the string buffer
    */
    private IPushbackReader reader = null;
    private final IStringBuffer isb;
    protected final LinkedList<IWord> wordPool;
    
    /**
     * the dictionary and task configuration
    */
    public final ADictionary dic;
    public final SegmenterConfig config;
    

    /**
     * method to create a new ISegment
     * 
     * @param   config
     * @param   dic
     */
    public DetectSeg(SegmenterConfig config, ADictionary dic)
    {
        this.config = config;
        this.dic    = dic;
        
        wordPool = new LinkedList<IWord>();
        isb = new IStringBuffer(64);
    }

    /**
     * @see ISegment#reset(Reader) 
     */
    @Override
    public void reset(Reader input) throws IOException 
    {
        if ( input != null ) {
            reader = new IPushbackReader(new BufferedReader(input));
        }
        
        idx = -1;
    }

    /**
     * @see ISegment#getStreamPosition() 
     */
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
    
    /**
     * get the current dictionary instance
     * 
     * @return  ADictionary
     */
    public ADictionary getDict() 
    {
        return dic;
    }
    
    /**
     * get the current task config instance
     */
    public SegmenterConfig getConfig() 
    {
        return config;
    }

    /**
     * @see     ISegment#next()
     * @return  IWord or null
     */
    @Override
    public IWord next() throws IOException 
    {
        /*
         * @Note: 
         * check and get the token directly from the word pool
         */
        if ( wordPool.size() > 0 ) {
            return wordPool.remove();
        }
        
        
        int c, i, pos;
        IWord   w = null;
        String  T = null;
        
        while ( (c = readNext()) != -1 ) {
            w = null;
            T = null;
            pos = idx;
            isb.clear();
            
            /* @Convertor: check if char is an Latin letter
             * and make the full-width half-width upperCase lowerCase
             * if it does
            */
            if ( StringUtil.isHWEnChar(c) || StringUtil.isFWEnChar(c) ) {
                if ( c > 65280 )          c -= 65248;
                if ( c >= 65 && c <= 90 ) c += 32;
            }
            isb.append((char)c);
            
            /*
             * get the temp string
             * and check T is a valid word in dictionary
            */
            T = isb.toString();
            if ( dic.match(ILexicon.CJK_WORD, T) ) {
                w = dic.get(ILexicon.CJK_WORD, T);
            }
            
            //forward maximum matching loop
            for ( i = 1; i < config.MAX_LENGTH; i++ ) {
                c = readNext();
                if ( c == -1 ) {
                    break;
                }
                
                //@see @Convertor
                if ( StringUtil.isHWEnChar(c) || StringUtil.isFWEnChar(c) ) {
                    if ( c > 65280 )             c -= 65248;
                    if ( c >= 65 && c <= 90 )     c += 32;
                }
                isb.append((char)c);
                
                //get the temp string
                T = isb.toString();
                //System.out.println(T);
                
                //check T is a valid word in dictionary
                if ( dic.match(ILexicon.CJK_WORD, T) ) {
                    w = dic.get(ILexicon.CJK_WORD, T);
                }
            }
            
            /* 
             * match no word in dictionary
             * push back the char that have read except the first one and continue the loop
             */
            if ( w == null ) {
                for ( i = isb.length() - 1; i > 0; i-- ) pushBack(isb.charAt(i));
                continue;
            }
            
            //-----------------------------------------------------
            
            /*
             * yat, match a item and return it as a segment result
             * also we need to push back the none-match part
             * @Note: we will not check the pinyin, part of speech, synonyms words
             * get the need? do it yourself here. @see ASegment#next() 
            */
            int LENGTH = w.getLength();
            for ( i = isb.length() - 1; i >= LENGTH; i-- ) {
                pushBack(isb.charAt(i));
            }
            
            //add position record
            w = w.clone();
            w.setPosition(pos);
            
            
            /* 
             * check and append the word features
             * 1, check and append the Pinyin
             * 2, check and append the synonyms
            */
            if ( config.APPEND_CJK_PINYIN 
                    && config.LOAD_CJK_PINYIN && w.getPinyin() != null ) {
                IWord pinyin = new Word(w.getPinyin(), IWord.T_CJK_PINYIN);
                pinyin.setPosition(pos);
                pinyin.setLength(w.getLength());
                wordPool.add(pinyin);
            }
            
            if ( config.APPEND_CJK_SYN 
                    && config.LOAD_CJK_SYN && w.getSyn() != null ) {
                SegKit.appendSynonyms(config, wordPool, w);
            }
            
            return w;
        }
        
        return null;
    }
    
}
