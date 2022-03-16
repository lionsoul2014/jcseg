package org.lionsoul.jcseg;

import java.io.IOException;
import java.io.Reader;

import org.lionsoul.jcseg.fi.SegmenterFunction;
import org.lionsoul.jcseg.segmenter.ComplexSeg;
import org.lionsoul.jcseg.segmenter.DelimiterSeg;
import org.lionsoul.jcseg.segmenter.DetectSeg;
import org.lionsoul.jcseg.segmenter.MostSeg;
import org.lionsoul.jcseg.segmenter.NGramSeg;
import org.lionsoul.jcseg.segmenter.NLPSeg;
import org.lionsoul.jcseg.segmenter.SimpleSeg;

/**
 * Jcseg segmentation interface
 * 
 * @author  chenxin<chenxin619315@gmail.com>
 */
public interface ISegment 
{
	/** Segmentation type constants */
	Type SIMPLE = Type.SIMPLE;
	Type COMPLEX = Type.COMPLEX;
	Type DETECT = Type.DETECT;
	Type MOST = Type.MOST;
	Type NLP = Type.NLP;
	Type DELIMITER = Type.DELIMITER;
	Type NGRAM = Type.NGRAM;
	
	enum Type
	{
		SIMPLE("simple", 1, SimpleSeg::new),
		COMPLEX("complex", 2, ComplexSeg::new),
		DETECT("detect", 3, DetectSeg::new),
		MOST("most", 4, MostSeg::new),
		NLP("nlp", 5, NLPSeg::new),
		DELIMITER("delimiter", 6, DelimiterSeg::new),
		NGRAM("ngram", 7, NGramSeg::new);
		
		public final String name;
		public final int index;
		public final SegmenterFunction factory;
    	
    	
        /**
         * the type index and type mapping
         * for quick get the type by type index number. 
        */
        public final static Type[] MAPPING = new Type[]{
        	null, SIMPLE, COMPLEX, DETECT, MOST, NLP, DELIMITER, NGRAM
        };
    	
    	private Type(String name, int index, SegmenterFunction factory)
    	{
    		this.name = name;
    		this.index = index;
    		this.factory = factory;
    	}
    	
        
        /**
         * get the Type with the specified string name
         * 
         * @param   type (All lowercase string)
         * @return  Type
        */
        public static Type fromString(String type, Type defaultValue)
        {
        	if ( type == null ) {
        		return defaultValue;
        	}
        	
			switch (type.toLowerCase()) {
			case "simple":
				return Type.SIMPLE;
			case "complex":
				return Type.COMPLEX;
			case "detect":
				return Type.DETECT;
			case "most":
				return Type.MOST;
			case "nlp":
				return Type.NLP;
			case "delimiter":
				return Type.DELIMITER;
			case "ngram":
				return Type.NGRAM;
			}
            
            return defaultValue;
        }
        
		public static Type fromString(String type)
		{
		    return fromString(type, Type.COMPLEX);
		}
		
		public static Type fromIndex(int index)
		{
			assert index > 0;
			assert index < MAPPING.length;
			return MAPPING[index];
		}
    }
	
	/** Segmentation type index */
	int SIMPLE_MODE = Type.SIMPLE.index;
	int COMPLEX_MODE = Type.COMPLEX.index;
	int DETECT_MODE = Type.DETECT.index;
	int MOST_MODE = Type.MOST.index;
	int NLP_MODE = Type.NLP.index;
	int DELIMITER_MODE = Type.DELIMITER.index;
	int NGRAM_MODE = Type.NGRAM.index;
	
	
    /** Whether to check the Chinese and English mixed word.*/
	int CHECK_CE_MASk = 0x01 << 0;
    /** Whether to check the Chinese fraction.*/
	int CHECK_CF_MASK = 0x01 << 1;
    /** Whether to start the Latin secondary segmentation.*/
	int START_SS_MASK = 0x01 << 2;
    
    
    /**
     * Whether to check the English Chinese mixed suffix
     * For the new implementation of the mixed word recognition 
     * 
     * Added at 2016/11/22
    */
	int CHECK_EC_MASK = 0x01 << 3;
    
    /** reset the reader */
	void reset(Reader input) throws IOException;
    
    /** get the current length of the stream */
	int getStreamPosition();
    
    /** segment a word from a char array from a specified position. */
	IWord next() throws IOException;
}
