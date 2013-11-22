package org.lionsoul.jcseg.lucene;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.lionsoul.jcseg.core.ADictionary;
import org.lionsoul.jcseg.core.ISegment;
import org.lionsoul.jcseg.core.IWord;
import org.lionsoul.jcseg.core.JcsegException;
import org.lionsoul.jcseg.core.JcsegTaskConfig;
import org.lionsoul.jcseg.core.SegmentFactory;


/**
 * jcsge tokennizer for lucene.
 * 
 * @author	chenxin<chenxin619315@gmail.com>
 */
public class JcsegTokenizer extends Tokenizer {
	
	private ISegment segmentor;
	
	private CharTermAttribute termAtt;
	private OffsetAttribute offsetAtt;
	
	public JcsegTokenizer(Reader input, int mode,
			JcsegTaskConfig config, ADictionary dic ) throws JcsegException, IOException {
		super(input);
		
		segmentor = SegmentFactory.createJcseg(mode, new Object[]{config, dic});
		segmentor.reset(input);
		termAtt = addAttribute(CharTermAttribute.class);
		offsetAtt = addAttribute(OffsetAttribute.class);
	}

	@Override
	public boolean incrementToken() throws IOException {
		clearAttributes();
		IWord word = segmentor.next();
		if ( word != null ) {
			termAtt.append(word.getValue());
			//termAtt.copyBuffer(word.getValue(), 0, word.getValue().length);
			termAtt.setLength(word.getLength());
			offsetAtt.setOffset(word.getPosition(), word.getPosition() + word.getLength());
			return true;
		} else {
			end();
			return false;
		}
	}
	
	@Override
	public void reset() throws IOException {
		super.reset();
		segmentor.reset(input);
	}
}
