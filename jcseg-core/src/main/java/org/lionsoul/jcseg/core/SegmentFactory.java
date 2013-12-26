package org.lionsoul.jcseg.core;

import java.io.Reader;
import java.lang.reflect.Constructor;

/**
 * Segment factory to create singleton ISegment
 * 		object.  a path of the class that has implemented the ISegment
 * interface must be given first.
 * 
 * @author	chenxin<chenxin619315@gmail.com>
 */
public class SegmentFactory {
	
	//current jcseg version.
	public static final String version = "1.9.2";
	
	/**
	 * load the ISegment class with the given path
	 * 
	 * @param 	__segClass
	 * @return ISegment
	 */
	public static ISegment createSegment( String __segClass,
				Class<?> paramtypes[], Object args[] ) {
		ISegment seg = null;
		try {
			Class<?> _class = Class.forName(__segClass);
			Constructor<?> cons = _class.getConstructor(paramtypes);
			seg = ( ISegment ) cons.newInstance(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("can't load the ISegment implements class " +
					"with path ["+__segClass+"] ");
		}
		return seg;
	}
	
	/**
	 * create the specified mode jcseg instance . <br />
	 * 
	 * @param	mode
	 * @return	ISegment
	 * @throws JcsegException 
	 */
	public static ISegment createJcseg( int mode, Object...args ) throws JcsegException {
		String __segClass;
		if ( mode == JcsegTaskConfig.SIMPLE_MODE )
			__segClass = "org.lionsoul.jcseg.SimpleSeg";
		else if ( mode == JcsegTaskConfig.COMPLEX_MODE )
			__segClass = "org.lionsoul.jcseg.ComplexSeg";
		else 
			throw new JcsegException("No Such Algorithm Excpetion");
		
		Class<?>[] _paramtype = null;
		if ( args.length == 2 ) {
			_paramtype = new Class[]{JcsegTaskConfig.class, ADictionary.class};
		} else if ( args.length == 3 ) {
			_paramtype = new Class[]{Reader.class, JcsegTaskConfig.class, ADictionary.class};
		} else {
			throw new JcsegException("length of the arguments should be 2 or 3");
		}
		
		return createSegment(__segClass, _paramtype, args);
	}
	
}
