package org.lionsoul.jcseg.elasticsearch.plugin.analysis.jcseg;

import java.util.Collection;
import java.util.Collections;

import org.elasticsearch.common.inject.Module;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.plugins.Plugin;
import org.lionsoul.jcseg.elasticsearch.index.analysis.JcsegAnalysisBinderProcessor;
import org.lionsoul.jcseg.elasticsearch.indices.analysis.jcseg.JcsegIndicesAnalysisModule;

/**
 * elasticsearch jcseg analysis plugin
 * 
 * @author    chenxin<chenxin619315@gmail.com>
 */
public class AnalysisJcsegPlugin extends Plugin {

    @Override public String name() {
        return "analysis-jcseg";
    }


    @Override public String description() {
        return "jcseg analysis support";
    }

    @Override
    public Collection<Module> nodeModules() {
        return Collections.<Module>singletonList(new JcsegIndicesAnalysisModule());
    }

    public void onModule(AnalysisModule module) {
        module.addProcessor(new JcsegAnalysisBinderProcessor());
    }
}
