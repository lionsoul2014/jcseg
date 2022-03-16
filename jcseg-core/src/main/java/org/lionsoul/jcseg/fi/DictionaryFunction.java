package org.lionsoul.jcseg.fi;

import org.lionsoul.jcseg.dic.ADictionary;
import org.lionsoul.jcseg.segmenter.SegmenterConfig;

@FunctionalInterface
public interface DictionaryFunction {
	ADictionary create(SegmenterConfig config, boolean sync);
}
