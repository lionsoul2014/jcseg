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
	public static final Type SIMPLE = Type.SIMPLE;
	public static final Type COMPLEX = Type.COMPLEX;
	public static final Type DETECT = Type.DETECT;
	public static final Type MOST = Type.MOST;
	public static final Type NLP = Type.NLP;
	public static final Type DELIMITER = Type.DELIMITER;
	public static final Type NGRAM = Type.NGRAM;
	
	public static enum Type 
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
        	
            type = type.toLowerCase();
            if ( "simple".equals(type) ) {
                return Type.SIMPLE;
            } else if ( "complex".equals(type) ) {
                return Type.COMPLEX;
            } else if ( "detect".equals(type) ) {
                return Type.DETECT;
            } else if ( "most".equals(type) ) {
                return Type.MOST;
            } else if ( "nlp".equals(type) ) {
                return Type.NLP;
            } else if ( "delimiter".equals(type) ) {
                return Type.DELIMITER;
            } else if ( "ngram".equals(type) ) {
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
	public static final int SIMPLE_MODE = Type.SIMPLE.index;
	public static final int COMPLEX_MODE = Type.COMPLEX.index;
	public static final int DETECT_MODE = Type.DETECT.index;
	public static final int MOST_MODE = Type.MOST.index;
	public static final int NLP_MODE = Type.NLP.index;
	public static final int DELIMITER_MODE = Type.DELIMITER.index;
	public static final int NGRAM_MODE = Type.NGRAM.index;
	
	
    /** Whether to check the Chinese and English mixed word.*/
    public static final int CHECK_CE_MASk = 1 << 0;
    /** Whether to check the Chinese fraction.*/
    public static final int CHECK_CF_MASK = 1 << 1;
    /** Whether to start the Latin secondary segmentation.*/
    public static final int START_SS_MASK = 1 << 2;
    
    
    /**
     * Whether to check the English Chinese mixed suffix
     * For the new implementation of the mixed word recognition 
     * 
     * Added at 2016/11/22
    */
    public static final int CHECK_EC_MASK = 1 << 3;
    
    /**
     * reset the reader
     * 
     * @param input
     */
    public void reset(Reader input) throws IOException;
    
    /**
     * get the current length of the stream
     * 
     * @return int
     */
    public int getStreamPosition();
    
    /**
     * segment a word from a char array
     *         from a specified position.
     * 
     * @return IWord
     */
    public IWord next() throws IOException;
}
