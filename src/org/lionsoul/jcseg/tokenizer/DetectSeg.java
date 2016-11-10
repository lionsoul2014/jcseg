package org.lionsoul.jcseg.tokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.ILexicon;
import org.lionsoul.jcseg.tokenizer.core.ISegment;
import org.lionsoul.jcseg.tokenizer.core.IWord;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;
import org.lionsoul.jcseg.util.StringUtil;
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
public class DetectSeg implements ISegment
{
    /**
     * the index of the current input stream 
    */
    private int idx;
    
    /**
     * runtime needed push back reader and the string buffer
    */
    private IPushbackReader reader = null;
    private IStringBuffer isb = null;
    
    /**
     * the dictionary and task configuration
    */
    private ADictionary dic;
    private JcsegTaskConfig config;
    
    /**
     * method to create the new ISegment
     * 
     * @param   config
     * @param   dic
     * @throws  IOException
     */
    public DetectSeg(JcsegTaskConfig config, ADictionary dic) throws IOException 
    {
        this(null, config, dic);
    }

    /**
     * method to create a new ISegment
     * 
     * @param   input
     * @param   config
     * @param   dic
     * @throws  IOException
     */
    public DetectSeg(Reader input, JcsegTaskConfig config, ADictionary dic) throws IOException 
    {
        this.config = config;
        this.dic    = dic;
        isb = new IStringBuffer(64);
        reset(input);    //reset the stream
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
     * @throws  IOException 
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
     * @throws  IOException 
     */
    protected void pushBack( int data ) throws IOException 
    {
        reader.unread(data);
        idx--;
    }
    
    /**
     * set the current dictionary instance
     * 
     * @param   dic
     */
    public void setDict( ADictionary dic ) 
    {
        this.dic = dic;
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
     * set the current task config
     * 
     * @param   config
     */
    public void setConfig( JcsegTaskConfig config ) 
    {
        this.config = config;
    }
    
    /**
     * get the current task config instance
     * 
     * @return  JcsegTaskConfig
     */
    public JcsegTaskConfig getConfig() 
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
        int c, i, pos;
        IWord   w = null;
        String  T = null;
        
        while ( (c = readNext()) != -1 ) {
            w = null;
            T = null;
            pos = idx;
            isb.clear();
            
            /* @Convertor: check if char is an latin letter
             * and make the full-width half-width uppercase lowercase
             * if it does
            */
            if ( StringUtil.isHWEnChar(c) || StringUtil.isFWEnChar(c) ) {
                if ( c > 65280 )             c -= 65248;
                if ( c >= 65 && c <= 90 )     c += 32;
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
                if ( c == -1 ) break;
                
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
             * push back the char readed except the first one and continue the loop
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
            
            return w;
        }
        
        return null;
    }
    
}
