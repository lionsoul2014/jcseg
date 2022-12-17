package org.lionsoul.jcseg.opensearch.index.analysis;

import org.lionsoul.jcseg.ISegment;
import org.lionsoul.jcseg.ISegment.Type;
import org.opensearch.common.settings.Settings;
import org.opensearch.env.Environment;
import org.opensearch.index.IndexSettings;

import java.io.IOException;

/**
 * Jcseg Detect Analyzer Provider
 * 
 * @author chenxin<chenxin619315@gmail.com>
 */
public class JcsegDetectAnalyzerProvider extends JcsegAnalyzerProvider
{
    public JcsegDetectAnalyzerProvider(
			IndexSettings indexSettings, Environment env, String name, Settings settings) throws IOException {
        super(indexSettings, env, name, settings);
    }

	@Override
	protected Type getType() {
		return ISegment.DETECT;
	}
    
}
