package org.lionsoul.jcseg.elasticsearch.plugin;

import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.common.io.PathUtils;
import org.elasticsearch.index.analysis.AnalyzerProvider;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.indices.analysis.AnalysisModule.AnalysisProvider;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;
import org.lionsoul.jcseg.elasticsearch.index.analysis.JcsegComplexAnalyzerProvider;
import org.lionsoul.jcseg.elasticsearch.index.analysis.JcsegDelimiterAnalyzerProvider;
import org.lionsoul.jcseg.elasticsearch.index.analysis.JcsegDetectAnalyzerProvider;
import org.lionsoul.jcseg.elasticsearch.index.analysis.JcsegNLPAnalyzerProvider;
import org.lionsoul.jcseg.elasticsearch.index.analysis.JcsegNoOpTokenFilterFactory;
import org.lionsoul.jcseg.elasticsearch.index.analysis.JcsegSearchAnalyzerProvider;
import org.lionsoul.jcseg.elasticsearch.index.analysis.JcsegSimpleAnalyzerProvider;
import org.lionsoul.jcseg.elasticsearch.index.analysis.JcsegTokenizerTokenizerFactory;
import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.DictionaryFactory;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;

/**
 * elasticsearch jcseg analysis plugin
 * 
 * @author  chenxin<chenxin619315@gmail.com>
 * @since   2017/01/08
 */
public class AnalysisJcsegPlugin extends Plugin implements AnalysisPlugin 
{
    @Override
    public Map<String, AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
        return singletonMap("jcseg_word", JcsegNoOpTokenFilterFactory::new);
    }

    @Override
    public Map<String, AnalysisProvider<TokenizerFactory>> getTokenizers() {
        return singletonMap("jcseg_tokenizer", JcsegTokenizerTokenizerFactory::new);
    }

    @Override
    public Map<String, AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> getAnalyzers() {
        Map<String, AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> analyzers = new HashMap<>();
        AnalysisProvider<AnalyzerProvider<? extends Analyzer>> searchAnalyzerProvider = JcsegSearchAnalyzerProvider::new;
        analyzers.put("jcseg", searchAnalyzerProvider);
        analyzers.put("jcseg_simple", JcsegSimpleAnalyzerProvider::new);
        analyzers.put("jcseg_complex", JcsegComplexAnalyzerProvider::new);
        analyzers.put("jcseg_detect", JcsegDetectAnalyzerProvider::new);
        analyzers.put("jcseg_search", searchAnalyzerProvider);
        analyzers.put("jcseg_nlp", JcsegNLPAnalyzerProvider::new);
        analyzers.put("jcseg_delimiter", JcsegDelimiterAnalyzerProvider::new);
        return analyzers;
    }




    /**
     * Quick interface to get a safe file path
     *
     * @param   file
     */
    private static final String pluginBase = AnalysisJcsegPlugin.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    private static final Path safePath = PathUtils.get(new File(pluginBase).getParent()).toAbsolutePath();
    public static final File getPluginSafeFile(String file)
    {
        return safePath.resolve(file).toFile();
    }

    /**
     * internal method to load the lexicon under the plugin directory
     *
     * @param   config
     * @param   dic
     */
    private static ADictionary dic = null;
    public static final synchronized ADictionary createSingletonDictionary(JcsegTaskConfig config) throws IOException {
        if ( dic != null ) {
            return dic;
        }

        config.setAutoload(false);  // Disable the default autoload for lexicon
        dic = DictionaryFactory.createDefaultDictionary(config);

        String[] lexPath = config.getLexiconPath();
        if ( lexPath == null ) {
            dic.loadClassPath();
            dic.resetSynonymsNet();
            return dic;
        }

        /* Check and load the lexicon from all the lexicon path */
        for ( String path : lexPath ) {
            final File safeDir = getPluginSafeFile(path);
            if ( ! safeDir.exists() ) {
                continue;
            }

            File[] files = safeDir.listFiles(new FilenameFilter(){
                @Override
                public boolean accept(File dir, String name) {
                    return (name.startsWith("lex-") && name.endsWith(".lex"));
                }
            });

            for ( File f : files ) {
                // System.out.println(f.getAbsolutePath());
                dic.load(getPluginSafeFile(f.getAbsolutePath()));
            }
        }

        if ( config.isAutoload() ) {
            dic.startAutoload();
        }

        dic.resetSynonymsNet();
        return dic;
    }

}
