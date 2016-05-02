package org.lionsoul.jcseg.elasticsearch.indices.analysis.jcseg;

import java.io.IOException;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.analysis.AnalyzerScope;
import org.elasticsearch.index.analysis.PreBuiltAnalyzerProviderFactory;
import org.elasticsearch.index.analysis.PreBuiltTokenFilterFactoryFactory;
import org.elasticsearch.index.analysis.PreBuiltTokenizerFactoryFactory;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.indices.analysis.IndicesAnalysisService;
import org.lionsoul.jcseg.analyzer.v5x.JcsegAnalyzer5X;
import org.lionsoul.jcseg.analyzer.v5x.JcsegTokenizer;
import org.lionsoul.jcseg.tokenizer.core.DictionaryFactory;
import org.lionsoul.jcseg.tokenizer.core.JcsegException;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;

/**
 * Registers indices level analysis components so, if not explicitly configured, will be shared
 * among all indices.
 */
public class JcsegIndicesAnalysis extends AbstractComponent
{
    @Inject
    public JcsegIndicesAnalysis(Settings settings, IndicesAnalysisService indicesAnalysisService)
    {
        super(settings);

        // Register jcseg analyzer
        indicesAnalysisService.analyzerProviderFactories().put(
            "jcseg", 
            new PreBuiltAnalyzerProviderFactory("jcseg", AnalyzerScope.INDICES, new JcsegAnalyzer5X(JcsegTaskConfig.COMPLEX_MODE))
        );

        // Register jcseg_tokenizer tokenizer
        indicesAnalysisService.tokenizerFactories().put("jcseg_tokenizer", 
                new PreBuiltTokenizerFactoryFactory(new TokenizerFactory() {
            @Override
            public String name() {
                return "jcseg_tokenizer";
            }

            @Override
            public Tokenizer create() {
                try {
                    JcsegTaskConfig config = new JcsegTaskConfig();
                    return new JcsegTokenizer(
                        JcsegTaskConfig.COMPLEX_MODE,
                        config,
                        DictionaryFactory.createSingletonDictionary(config)
                    );
                } catch (JcsegException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                return null;
            }
        }));

        // Register jcseg_word token filter
        indicesAnalysisService.tokenFilterFactories().put("jcseg_word", 
                new PreBuiltTokenFilterFactoryFactory(new TokenFilterFactory() {
            @Override
            public String name() {
                return "jcseg_word";
            }

            @Override
            public TokenStream create(TokenStream tokenStream) {
                return tokenStream;
            }
        }));
    }
}
