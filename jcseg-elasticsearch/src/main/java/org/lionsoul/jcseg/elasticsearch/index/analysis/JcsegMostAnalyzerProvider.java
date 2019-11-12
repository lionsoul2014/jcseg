package org.lionsoul.jcseg.elasticsearch.index.analysis;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.lionsoul.jcseg.ISegment.Type;
import org.lionsoul.jcseg.ISegment;

import java.io.IOException;

/**
 * Jcseg Search Analyzer Provider
 * 
 * @author chenxin<chenxin619315@gmail.com>
 */
public class JcsegMostAnalyzerProvider extends JcsegAnalyzerProvider
{
    public JcsegMostAnalyzerProvider(IndexSettings indexSettings,
            Environment env, String name, Settings settings) throws IOException {
        super(indexSettings, env, name, settings);
    }

	@Override
	protected Type getType() {
		return ISegment.MOST;
	}
    
}
