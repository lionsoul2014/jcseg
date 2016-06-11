package org.lionsoul.jcseg.tokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.lionsoul.jcseg.util.StringUtil;
import org.lionsoul.jcseg.util.IPushbackReader;
import org.lionsoul.jcseg.util.IStringBuffer;

/**
 * document sentence splitter
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class SentenceSeg 
{
    private static final int MAX_QUOTE_LENGTH = 15;
    
    //current position for the specifield stream
    protected int idx;
    
    //protected PushbackReader reader = null;
    protected IPushbackReader reader = null;
    
    /**
     * global string buffer 
    */
    protected IStringBuffer gisb = null;
        
    /**
     * construct method
     * 
     * @param  reader
     * @throws IOException 
    */
    public SentenceSeg(Reader reader) throws IOException
    {
        gisb = new IStringBuffer();
        reset(reader);
    }
    
    public SentenceSeg() throws IOException
    {
        this(null);
    }
    
    
    /**
     * stream/reader reset.
     * 
     * @param input
     * @throws IOException
     */
    public void reset( Reader input ) throws IOException
    { 
        if ( input != null ) 
            reader = new IPushbackReader(new BufferedReader(input));
        idx = -1;
    }
    
    /**
     * read the next char from the current position
     * 
     * @throws IOException 
     */
    protected int readNext() throws IOException 
    {    
        int c = reader.read();
        if ( c != -1 ) idx++;
        return c;
    }
    
    /**
     * get the next sentence
     * 
     * @return    Sentence
     * @throws    IOException 
    */
    public Sentence next() throws IOException
    {
        gisb.clear();
        int c, pos = -1;
        
        while ( (c = readNext()) != -1 ) {
            //clear the whitespace of the begainning
            if ( StringUtil.isWhitespace(c) ) continue;
            if ( c == '\n' || c == '\t' || c == '…' ) continue;
            if ( StringUtil.isCnPunctuation(c) ) {
                switch ( (char)c ) {
                case '“':
                case '【':
                case '（':
                case '《':
                case '｛':
                    break;
                default: continue;
                }
            }
            
            if ( StringUtil.isEnPunctuation(c)) {
                switch ( (char)c ) {
                case '"':
                /*case '[':
                case '(':
                case '{':
                case '<':*/
                    break;
                default: continue;
                }
            }
             
            pos = idx;
            gisb.clear().append((char)c);
            
            while ( (c = readNext()) != -1 ) {
                boolean endTag = false;
                
                /*
                 * here, define the sentence end tag
                 * punctuation like the following:
                 * .。\n;；?？!！:： 
                */
                switch ((char)c) {
                case '"': gisb.append('"'); readUntil('"');  break;
                case '“': gisb.append('“'); readUntil('”');  break;
                case '【': gisb.append('【'); readUntil('】'); break;
                case '《': gisb.append('《'); readUntil('》'); break;
                case '.': {
                    int chr = readNext();
                    gisb.append((char)c);
                    if ( StringUtil.isEnLetter(chr) )  {
                        reader.unread(chr);
                        continue;
                    } else {
                        endTag = true;
                    }
                    
                    break;
                }
                case '。':
                case ';':
                case '；':
                case '?':
                case '？':
                case '!':
                case '！':
                case '…': {
                    endTag = true;
                    gisb.append((char)c);
                    break;
                }
                case ':':
                case '：':
                case '\n': {
                    endTag = true;
                    break;
                }
                default:
                    gisb.append((char)c);
                }
                
                if ( endTag ) break;
            }

            //clear the whitespace from the back
            for ( int i = gisb.length() - 1; i >= 0; i-- ) {
                char chr = gisb.charAt(i);
                if ( chr == ' ' || chr == '\t' ) gisb.deleteCharAt(i);
                else break;
            }
            
            if ( gisb.length() <= 1 ) continue;
            return new Sentence(gisb.toString(), pos);
        }
                
        return null;
    }
    
    /**
     * loop the reader until the specifield char is found.
     * 
     * @param    echar
     * @throws  IOException  
    */
    protected void readUntil(char echar) throws IOException
    {
        int ch, i = 0;
        IStringBuffer sb = new IStringBuffer();
        while ( (ch = readNext()) != -1 ) {
            if ( ++i >= MAX_QUOTE_LENGTH ) {
                /*
                 * push back the readed chars
                 * and reset the global idx value. 
                */
                for ( int j = sb.length() - 1; j >= 0; j-- ) {
                    reader.unread(sb.charAt(j));
                }
                idx -= sb.length();
                break;
            }
            
            sb.append((char)ch);
            if ( ch == echar ) {
                gisb.append(sb.toString());
                break;
            }
        }
        
        sb = null;
    }
}
