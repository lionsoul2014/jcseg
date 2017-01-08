package org.lionsoul.jcseg.elasticsearch.plugin;

import org.apache.lucene.analysis.Analyzer;
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

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;

/**
 * elasticsearch jcseg analysis plugin
 * 
 * @author  chenxin<chenxin619315@gmail.com>
 * @date    2017/01/08
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
}
