package org.lionsoul.jcseg.elasticsearch.indices.analysis.jcseg;

import java.io.File;
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
import org.lionsoul.jcseg.elasticsearch.util.CommonUtil;
import org.lionsoul.jcseg.tokenizer.core.ADictionary;
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
        File proFile = new File("plugins/jcseg/jcseg.properties");
        JcsegTaskConfig config = proFile.exists() ? new JcsegTaskConfig(proFile.getPath()) : new JcsegTaskConfig(true);
        ADictionary dic = DictionaryFactory.createSingletonDictionary(config);
        
        //default
        indicesAnalysisService.analyzerProviderFactories().put(
            "jcseg", 
            new PreBuiltAnalyzerProviderFactory(
                "jcseg", 
                AnalyzerScope.GLOBAL, 
                new JcsegAnalyzer5X(CommonUtil.getSegMode(settings, JcsegTaskConfig.SEARCH_MODE), config, dic)
            )
        );
        
        //complex mode
        indicesAnalysisService.analyzerProviderFactories().put(
            "jcseg_complex", 
            new PreBuiltAnalyzerProviderFactory(
                "jcseg", 
                AnalyzerScope.GLOBAL, 
                new JcsegAnalyzer5X(JcsegTaskConfig.COMPLEX_MODE, config, dic)
            )
        );
        
        //simple mode
        indicesAnalysisService.analyzerProviderFactories().put(
            "jcseg_simple",
            new PreBuiltAnalyzerProviderFactory(
                "jcseg", 
                AnalyzerScope.GLOBAL, 
                new JcsegAnalyzer5X(JcsegTaskConfig.SIMPLE_MODE, config, dic)
            )
        );
        
        //detect mode
        indicesAnalysisService.analyzerProviderFactories().put(
            "jcseg_detect",
            new PreBuiltAnalyzerProviderFactory(
                "jcseg", 
                AnalyzerScope.GLOBAL, 
                new JcsegAnalyzer5X(JcsegTaskConfig.DETECT_MODE, config, dic)
            )
        );
        
        //most mode
        indicesAnalysisService.analyzerProviderFactories().put(
            "jcseg_search",
            new PreBuiltAnalyzerProviderFactory(
                "jcseg", 
                AnalyzerScope.GLOBAL, 
                new JcsegAnalyzer5X(JcsegTaskConfig.SEARCH_MODE, config, dic)
            )
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
                    /*
                     * @Added at 2016-05-02
                     * default to load the jcseg.properties configuration file
                     * in the jcseg plugin base directory {ES_HOME}/plugins/jcseg/
                    */
                    File proFile = new File("plugins/jcseg/jcseg.properties");
                    JcsegTaskConfig config = proFile.exists() ? new JcsegTaskConfig(proFile.getPath()) : new JcsegTaskConfig(true);
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
