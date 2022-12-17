package org.lionsoul.jcseg.segmenter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;

import org.lionsoul.jcseg.ISegment;
import org.lionsoul.jcseg.IWord;
import org.lionsoul.jcseg.dic.ADictionary;
import org.lionsoul.jcseg.dic.ILexicon;
import org.lionsoul.jcseg.util.IPushbackReader;
import org.lionsoul.jcseg.util.IStringBuffer;
import org.lionsoul.jcseg.util.StringUtil;

/**
 * delimiter segment algorithm implementation
 *  extended from common segment interface ISegment
 * 
 * @author  chenxin<chenxin619315@gmail.com>
*/
public class DelimiterSeg implements ISegment
{ 
    /**
     * the index of the current input stream 
    */
    private int idx;
    
    /**
     * the delimiter char
    */
    private char delimiter;
    
    /**
     * runtime needed push back reader and the string buffer
    */
    private IPushbackReader reader;
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
    public DelimiterSeg(SegmenterConfig config, ADictionary dic)
    {
        this.config    = config;
        this.dic       = dic;
        this.delimiter = config.getDELIMITER();
        
        wordPool = new LinkedList<>();
        isb      = new IStringBuffer(64);
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
        
        while ( (c = readNext()) != -1 ) {
            /*
             * clear the delimiter at this position
            */
            if ( c == delimiter ) {
                continue;
            }
            
            pos = idx;
            isb.clear().append((char)c);
            for ( i = 1; i < config.MAX_LATIN_LENGTH; i++ ) {
                c = readNext();
                if ( c == -1 || c == delimiter ) {
                    break;
                }
                
                isb.append((char)c);
            }
            
            //System.out.println(isb.toString());
            /* @Note
             * check and do the word return
             * 1, if the global dictionary instance is more effective, we will
             * try to get more info from the dictionary
             * 2, otherwise create a new word with just position filled
            */
            IWord wd = null;
            final String val = isb.toString();
            if ( dic != null && dic.match(ILexicon.CJK_WORD, val) ) {
                wd = dic.get(ILexicon.CJK_WORD, val).clone();
            } else {
                wd = new Word(val, IWord.T_CJK_WORD);
                if ( StringUtil.isLatin(val) ) {
                    wd.setPartSpeechForNull(IWord.EN_POSPEECH);
                }
            }
            
            //reset the word offset
            wd.setPosition(pos);
            
            /* 
             * check and append the word features
             * 1, check and append the Pinyin
             * 2, check and append the synonyms
            */
            if ( dic != null && config.APPEND_CJK_PINYIN 
                    && config.LOAD_CJK_PINYIN && wd.getPinyin() != null ) {
                final IWord pinyin = new Word(wd.getPinyin(), IWord.T_CJK_PINYIN);
                pinyin.setPosition(pos);
                pinyin.setLength(wd.getLength());
                wordPool.add(pinyin);
            }
            
            if ( dic != null && config.APPEND_CJK_SYN 
                    && config.LOAD_CJK_SYN && wd.getSyn() != null ) {
                SegKit.appendSynonyms(config, wordPool, wd);
            }
            
            return wd;
        }
        
        return null;
    }

    /**
     * get the current delimiter 
     * 
     * @return  char
    */
    public char getDelimiter()
    {
        return delimiter;
    }

    /**
     * set the delimiter default to whitespace
     * 
     * @param   delimiter
    */
    public void setDelimiter(char delimiter)
    {
        this.delimiter = delimiter;
    }

    /**
     * get the current dictionary instance
     * 
     * @return  ADictionary
    */
    public ADictionary getDic()
    {
        return dic;
    }

    /**
     * get the current Segmenter Config instance
     * 
     * @return  SegmenterConfig
    */
    public SegmenterConfig getConfig()
    {
        return config;
    }
    
}
