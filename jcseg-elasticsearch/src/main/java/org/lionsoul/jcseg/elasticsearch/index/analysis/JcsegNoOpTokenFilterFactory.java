package org.lionsoul.jcseg.elasticsearch.index.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;

public class JcsegNoOpTokenFilterFactory extends AbstractTokenFilterFactory
{
    
    public JcsegNoOpTokenFilterFactory(IndexSettings indexSettings,
            Environment env, String name, Settings settings)
    {
        super(indexSettings, name, settings);
    }

    @Override
    public TokenStream create(TokenStream tokenStream)
    {
        return tokenStream;
    }

}
