package org.lionsoul.jcseg.analyzer;

import java.io.IOException;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.lionsoul.jcseg.ISegment;
import org.lionsoul.jcseg.IWord;
import org.lionsoul.jcseg.dic.ADictionary;
import org.lionsoul.jcseg.segmenter.SegmenterConfig;


/**
 * <p>
 * here is the documentation from {@link org.apache.lucene.analysis.Tokenizer}
 * A Tokenizer is a TokenStream whose input is a Reader.
 * </p>
 * 
 * <p>
 * This is an abstract class; subclasses must override {@link #incrementToken()}
 * <p>
 * 
 * <p>
 * NOTE: Subclasses overriding {@link #incrementToken()} must
 * call {@link #clearAttributes()} before setting attributes
 * </p>
 *
 * <p>
 * lucene invoke Tokenizer#setReader(Reader input) to set the inputPending
 * after invoke the reset, global object input will be available
 * </p>
 * 
 * <p>Jcseg tokenizer for lucene on or after 5.1.0</p>
 * 
 * @author    chenxin<chenxin619315@gmail.com>
 */
public class JcsegTokenizer extends Tokenizer 
{
    // The default Jcseg segmentor
    private final ISegment segmentor;

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);
    
    /**
     * field level offset tracker for multiple-value field
     * like the Array field in Elasticsearch or Solr
    */
    private int fieldOffset = 0;
    
    public JcsegTokenizer(
        ISegment.Type type,
        SegmenterConfig config,
        ADictionary dic )
    {
        segmentor = type.factory.create(config, dic);
    }

    @Override
    final public boolean incrementToken() throws IOException 
    {
    	/* Clear the attributes */
    	clearAttributes();
    	
        final IWord word = segmentor.next();
        if ( word == null ) {
            fieldOffset = offsetAtt.endOffset();
            return false;
        }
        
        /// char[] token = word.getValue().toCharArray();
        /// termAtt.copyBuffer(token, 0, token.length);
        termAtt.append(word.getValue());
        termAtt.setLength(word.getLength());
        offsetAtt.setOffset(
        	correctOffset(fieldOffset + word.getPosition()), 
        	correctOffset(fieldOffset + word.getPosition() + word.getLength())
        );
        typeAtt.setType("word");
        
        return true;
    }
    
    @Override
    public void end() throws IOException
    {
        super.end();
        final int finalOffset = correctOffset(fieldOffset);
        offsetAtt.setOffset(finalOffset, finalOffset);
        this.fieldOffset = 0;
    }
    
    @Override
    public void reset() throws IOException 
    {
        super.reset();
        segmentor.reset(input);
    }
    
}
