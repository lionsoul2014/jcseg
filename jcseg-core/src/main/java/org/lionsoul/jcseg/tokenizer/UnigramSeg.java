package org.lionsoul.jcseg.tokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.ISegment;
import org.lionsoul.jcseg.tokenizer.core.IWord;
import org.lionsoul.jcseg.util.IPushbackReader;
import org.lionsoul.jcseg.util.StringUtil;

/**
 * Jcseg Unigram tokenizer implementation
 * 
 * @author lionsoul<chenxin619315@gmail.com>
 * @since  2.3.0
*/

public class UnigramSeg implements ISegment
{
    /**
     * the index value of the current input stream
     * mainly for track the start position of the token
    */
    protected int idx;
    protected IPushbackReader reader = null;
    protected ADictionary dic = null;
    
    /**
     * default constructor
     * 
     * @param  input
     * @throws IOException 
    */
    public UnigramSeg(Reader input, ADictionary dic) throws IOException
    {
        this.dic = dic;
        reset(input);
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

    @Override
    public IWord next() throws IOException
    {
        int c;
        while ( (c = readNext()) != -1 ) {
            if ( StringUtil.isWhitespace(c) ) continue;
            
            /* CJK char.
             * and start the CJK word analysis
            */
            if ( StringUtil.isCJKChar( c ) ) {
            }
            /* English/Latin char.
             * and start the Latin word analysis
            */
            else if ( StringUtil.isEnChar(c) ) {
            }
            /* letter number like 'ⅠⅡ';
             */
            else if ( StringUtil.isLetterNumber(c) ) {
            }
            /* other number like '①⑩⑽㈩';
             */
            else if ( StringUtil.isOtherNumber(c) ) {
            }
        }
        
        return null;
    }

}
