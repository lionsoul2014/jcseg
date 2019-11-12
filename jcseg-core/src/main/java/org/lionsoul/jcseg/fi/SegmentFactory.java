package org.lionsoul.jcseg.fi;

import org.lionsoul.jcseg.ISegment;
import org.lionsoul.jcseg.JcsegTaskConfig;
import org.lionsoul.jcseg.dic.ADictionary;

@FunctionalInterface
public interface SegmentFactory {
	public ISegment create(JcsegTaskConfig config, ADictionary dic);
}
