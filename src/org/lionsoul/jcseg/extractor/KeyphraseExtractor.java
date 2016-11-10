package org.lionsoul.jcseg.extractor;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.lionsoul.jcseg.tokenizer.core.ISegment;
import org.lionsoul.jcseg.tokenizer.core.IWord;

/**
 * key phrase extractor 
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public abstract class KeyphraseExtractor 
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
    public KeyphraseExtractor( ISegment seg )
    {
        this.seg = seg;
    }
    
    /**
     * word item filter
     * 
     * @param   work
     * @return  boolean
    */
    protected boolean filter(IWord word)
    {
        /*
         * normally word with length less than 2 will
         * be something, well could be ignored 
        */
        /*if ( word.getValue().length() < 2 ) 
        {
            return false;
        }*/
        
        //type check
        switch ( word.getType() ) {
            case IWord.T_BASIC_LATIN:
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
            /*case 'm':
            {
                if ( poss[0].equals("mix") ) return true;
                return false;
            }*/
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
            //case 'h':
            //case 'k':
            case 'g':
            case 'x':
            case 'w': {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * get the keyphrase list from a string
     * 
     * @param   doc
     * @return  List[]
     * @throws  IOException
    */
    public List<String> getKeyphraseFromString(String doc) throws IOException
    {
        return getKeyphrase(new StringReader(doc));
    }
    
    /**
     * get the keyphrase list from a file 
     * 
     * @param   file
     * @return  List[]
     * @throws  IOException
    */
    public List<String> getKeyphraseFromFile(String file) throws IOException
    {
        return getKeyphrase(new FileReader(file));
    }
    
    /**
     * get the keyphrase list from a reader
     * 
     * @param   reader
     * @throws  IOException
    */
    public abstract List<String> getKeyphrase(Reader reader) throws IOException;

    
    public ISegment getSeg()
    {
        return seg;
    }

    public void setSeg(ISegment seg)
    {
        this.seg = seg;
    }
}
