package org.lionsoul.jcseg.analyzer.v5x;

import java.io.IOException;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
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
 * here is the documentation from {@link org.apache.lucene.analysis.tokenizer}
 * A Tokenizer is a TokenStream whose input is a Reader.
 * </p>
 * 
 * <p>
 * This is an abstract class; subclasses must override {@link #incrementToken()}
 * <p>
 * 
 * <p>
 * NOTE: Subclasses overriding {@link #incrementToken()} must
 * call {@link AttributeSource#clearAttributes()} before setting attributes
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
    //the default jcseg segmentor
    private ISegment segmentor;

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);
    
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
        clearAttributes();
        IWord word = segmentor.next();
        
        if ( word == null )
        {
            end();
            return false;
        }
        
        //termAtt.append(word.getValue());
        //termAtt.setLength(word.getLength());
        
        char[] token = word.getValue().toCharArray();
        termAtt.copyBuffer(token, 0, token.length);
        offsetAtt.setOffset(word.getPosition(), word.getPosition() + word.getLength());
        typeAtt.setType("word");
        
        return true;
    }
    
    @Override
    public void reset() throws IOException 
    {
        super.reset();
        segmentor.reset(input);
    }
}
