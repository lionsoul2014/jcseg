package org.lionsoul.jcseg.elasticsearch.index.analysis;

import org.elasticsearch.index.analysis.AnalysisModule;

/**
 * JcsegAnalysisBinderProcessor manager class
 * 
 * @author chenxin<chenxin619315@gmail.com>
 */
public class JcsegAnalysisBinderProcessor extends AnalysisModule.AnalysisBinderProcessor
{

    @Override public void processAnalyzers(AnalyzersBindings analyzersBindings)
    {
        analyzersBindings.processAnalyzer("jcseg", JcsegAnalyzerProvider.class);
        super.processAnalyzers(analyzersBindings);
    }

    @Override public void processTokenizers(TokenizersBindings tokenizersBindings)
    {
        tokenizersBindings.processTokenizer("jcseg", JcsegTokenizerFactory.class);
        super.processTokenizers(tokenizersBindings);
    }

    @Override public void processTokenFilters(TokenFiltersBindings tokenFiltersBindings)
    {
        super.processTokenFilters(tokenFiltersBindings);
    }
    
}
