package org.lionsoul.jcseg.elasticsearch.indices.analysis.jcseg;

import org.elasticsearch.common.inject.AbstractModule;

/**
 */
public class JcsegIndicesAnalysisModule extends AbstractModule
{
    @Override
    protected void configure() {
        bind(JcsegIndicesAnalysis.class).asEagerSingleton();
    }
}