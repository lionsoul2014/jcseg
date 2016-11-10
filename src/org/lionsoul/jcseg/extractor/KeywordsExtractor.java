package org.lionsoul.jcseg.extractor;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.lionsoul.jcseg.tokenizer.core.ISegment;
import org.lionsoul.jcseg.tokenizer.core.IWord;

/**
 * document keywords extractor
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public abstract class KeywordsExtractor 
{
    /**
     * the ISegment object
    */
    protected ISegment seg;
    
    /**
     * construct method 
     * 
     * @param    seg
    */
    public KeywordsExtractor( ISegment seg )
    {
        this.seg = seg;
    }
    
    /**
     * word item filter
     * 
     * @param    word
    */
    protected boolean filter(IWord word)
    {
        /*
         * normally word with length less than 2 will
         * be something, well could be ignored 
        */
        if ( word.getValue().length() < 2 ) {
            return false;
        }
        
        //type check
        switch ( word.getType() ) {
            //case IWord.T_BASIC_LATIN:
            case IWord.T_LETTER_NUMBER:
            case IWord.T_OTHER_NUMBER:
            case IWord.T_CJK_PINYIN:
            case IWord.T_PUNCTUATION:
            case IWord.T_UNRECOGNIZE_WORD: {
                return false;
            }
        }
        
        //part of speech check
        String[] poss = word.getPartSpeech();
        if ( poss == null ) return true;
        
        char pos = poss[0].charAt(0);
        switch ( pos ) {
            case 'e': {
                if ( poss[0].equals("en") ) return true;
                return false;
            }
            case 'm': {
                if ( poss[0].equals("mix") ) return true;
                return false;
            }
            case 'q':
            case 'b':
            case 'r':
            case 'z':
            case 'p':
            case 'c':
            case 'u':
            case 'y':
            case 'd':    //@date 2015-11-23
            case 'o':
            case 'h':
            case 'k':
            case 'g':
            case 'x':
            case 'w': {
                return false;
            }
            
            /*case 'n':
            case 'v':
            case 'a':
            case 't':
            case 's':
            case 'f':
            {
                return true;
            }*/
        }
        
        return true;
    }
    
    /**
     * get the keywords list from a string
     * 
     * @param   doc
     * @return  List[]
     * @throws  IOException
    */
    public List<String> getKeywordsFromString(String doc) throws IOException
    {
        return getKeywords(new StringReader(doc));
    }
    
    /**
     * get the keywords list from a file 
     * 
     * @param   file
     * @return  List[]
     * @throws  IOException
    */
    public List<String> getKeywordsFromFile(String file) throws IOException
    {
        return getKeywords(new FileReader(file));
    }
    
    /**
     * get the keywords list from a reader
     * 
     * @param   reader
     * @throws  IOException
    */
    public abstract List<String> getKeywords(Reader reader) throws IOException;

    
    public ISegment getSeg()
    {
        return seg;
    }

    public void setSeg(ISegment seg)
    {
        this.seg = seg;
    }
}
