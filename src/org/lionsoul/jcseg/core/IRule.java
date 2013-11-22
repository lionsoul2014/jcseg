package org.lionsoul.jcseg.core;

/**
 * filter rule interface.
 * 		the most important concept for mmseg chinese
 * 	segment algorithm.
 * 
 * @author	chenxin<chenxin619315@gmail.com>
 */
public interface IRule {
	/**
	 * do the filter work
	 * 
	 * @param chunks
	 * @return IChunk[]
	 */
	public IChunk[] call( IChunk[] chunks );
}
