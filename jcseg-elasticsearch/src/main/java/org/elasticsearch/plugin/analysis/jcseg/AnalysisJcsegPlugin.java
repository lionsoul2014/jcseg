package org.elasticsearch.plugin.analysis.jcseg;

import org.elasticsearch.common.inject.Module;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.index.analysis.JcsegAnalysisBinderProcessor;
import org.elasticsearch.plugins.AbstractPlugin;

/*
 * elasticsearch jcseg analysis plugin
 * 
 * @author  chenxin<chenxin619315@gmail.com>
 */
public class AnalysisJcsegPlugin extends AbstractPlugin {

    @Override
    public String name() {
        return "analysis-jcseg";
    }

    @Override
    public String description() {
        return "jcseg analysis";
    }

    @Override
    public void processModule(Module module) {
        if (module instanceof AnalysisModule) {
            AnalysisModule analysisModule = (AnalysisModule) module;
            analysisModule.addProcessor(new JcsegAnalysisBinderProcessor());
        }
    }
}
