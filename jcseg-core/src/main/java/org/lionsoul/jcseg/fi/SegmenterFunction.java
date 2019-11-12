package org.lionsoul.jcseg.fi;

import org.lionsoul.jcseg.ISegment;
import org.lionsoul.jcseg.dic.ADictionary;
import org.lionsoul.jcseg.segmenter.SegmenterConfig;

@FunctionalInterface
public interface SegmenterFunction {
	public ISegment create(SegmenterConfig config, ADictionary dic);
}
