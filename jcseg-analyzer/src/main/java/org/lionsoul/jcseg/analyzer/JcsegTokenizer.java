package org.lionsoul.jcseg.analyzer;

import java.io.IOException;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttributeImpl;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.ISegment;
import org.lionsoul.jcseg.tokenizer.core.IWord;
import org.lionsoul.jcseg.tokenizer.core.JcsegException;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;
import org.lionsoul.jcseg.tokenizer.core.SegmentFactory;


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
 * <p>jcseg tokennizer for lucene on or after 5.1.0</p>
 * 
 * @author    chenxin<chenxin619315@gmail.com>
 */
public class JcsegTokenizer extends Tokenizer 
{
    // The default Jcseg segmentor
    private final ISegment segmentor;

    private final CharTermAttributeImpl termAtt = (CharTermAttributeImpl)addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);
    
    /**
     * field level offset tracker for multiple-value field
     * like the Array field in Elasticseach 
    */
    // private int fieldOffset = 0;
    
    /** end position for the last word token */
    private int endPosition = 0;
    
    public JcsegTokenizer(
        int mode,
        JcsegTaskConfig config,
        ADictionary dic ) throws JcsegException, IOException 
    {
        segmentor = SegmentFactory.createJcseg(mode, new Object[]{config, dic});
        segmentor.reset(input);
    }

    @Override
    final public boolean incrementToken() throws IOException 
    {
    	/* Clear the attributes */
    	clearAttributes();
    	
        final IWord word = segmentor.next();
        if ( word == null ) {
            return false;
        }
        
        //char[] token = word.getValue().toCharArray();
        //termAtt.copyBuffer(token, 0, token.length);
        termAtt.clear();
        termAtt.append(word.getValue());
        final int endPos = word.getPosition() + word.getLength();
        offsetAtt.setOffset(
        	endPosition + correctOffset(word.getPosition()), 
        	endPosition + correctOffset(endPos)
        );
        endPosition = endPos;
        typeAtt.setType("word");
        
        return true;
    }
    
    @Override
    public void end() throws IOException
    {
        super.end();
        final int finalOffset = correctOffset(endPosition);
        offsetAtt.setOffset(finalOffset, finalOffset);
        endPosition = 0;
    }
    
    @Override
    public void reset() throws IOException 
    {
        super.reset();
        segmentor.reset(input);
    }
    
}
