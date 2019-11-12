package org.lionsoul.jcseg.fi;

import org.lionsoul.jcseg.dic.ADictionary;
import org.lionsoul.jcseg.segmenter.SegmenterConfig;

@FunctionalInterface
public interface DictionaryFunction {
	public ADictionary create(SegmenterConfig config, boolean sync);
}
