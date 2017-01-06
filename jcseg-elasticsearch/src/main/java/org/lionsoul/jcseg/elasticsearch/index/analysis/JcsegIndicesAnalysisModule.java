package org.lionsoul.jcseg.elasticsearch.index.analysis;

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