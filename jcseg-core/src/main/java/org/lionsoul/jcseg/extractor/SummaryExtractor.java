package org.lionsoul.jcseg.extractor;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.lionsoul.jcseg.tokenizer.SentenceSeg;
import org.lionsoul.jcseg.tokenizer.core.ISegment;

/**
 * document summary extractor 
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public abstract class SummaryExtractor 
{
    /**
     * ISegment word tokenizer object 
    */
    protected ISegment wordSeg;
    
    /**
     * sentence splitter object 
    */
    protected SentenceSeg sentenceSeg;
    
    /**
     * construct method
     * 
     * @param   wordSeg
     * @param   sentenceSeg
    */
    public SummaryExtractor(ISegment wordSeg, SentenceSeg sentenceSeg)
    {
        this.wordSeg = wordSeg;
        this.sentenceSeg = sentenceSeg;
    }
    
    /**
     * get key sentence from a string
     * 
     * @param   doc
     * @return  List<String>
     * @throws  IOException
    */
    public List<String> getKeySentenceFromString( String doc ) throws IOException
    {
        return getKeySentence(new StringReader(doc));
    }
    
    /**
     * get key sentence from a file path
     * 
     * @param   doc
     * @return  List<String>
     * @throws  IOException
    */
    public List<String> getKeySentenceFromFile( String file ) throws IOException
    {
        return getKeySentence(new FileReader(file));
    }
    
    /**
     * get the key sentence from a reader
     * 
     * @param   reader
     * @return  String
     * @throws  IOException
    */
    public abstract List<String> getKeySentence( Reader reader ) throws IOException;
    
    
    /**
     * get document summary from a string
     * 
     * @param   doc
     * @param   length
     * @return  String
     * @throws  IOException
    */
    public String getSummaryFromString(String doc, int length) throws IOException
    {
        return getSummary(new StringReader(doc), length);
    }
    
    
    /**
     * get document summary from a file
     * 
     * @param   file
     * @param   length
     * @return  String
     * @throws  IOException
    */
    public String getSummaryFromFile(String file, int length) throws IOException
    {
        return getSummary(new FileReader(file), length);
    }
    
    /**
     * get summary from a reader
     * 
     * @param   reader
     * @param   length
     * @return  String
     * @throws  IOException
    */
    public abstract String getSummary(Reader reader, int length) throws IOException;

    
    public ISegment getWordSeg()
    {
        return wordSeg;
    }

    public void setWordSeg(ISegment wordSeg)
    {
        this.wordSeg = wordSeg;
    }

    public SentenceSeg getSentenceSeg()
    {
        return sentenceSeg;
    }

    public void setSentenceSeg(SentenceSeg sentenceSeg)
    {
        this.sentenceSeg = sentenceSeg;
    }
    
}
