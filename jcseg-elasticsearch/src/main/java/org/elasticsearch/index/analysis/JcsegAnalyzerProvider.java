/*
 * Licensed to Elastic Search and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Elastic Search licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.index.analysis;

import java.io.File;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.settings.IndexSettings;
import org.lionsoul.jcseg.analyzer.JcsegAnalyzer4X;
import org.lionsoul.jcseg.core.JcsegTaskConfig;

/**
 * JcsegAnalyzerProvider
 * 
 * @author chenxin<chenxin619315@gmail.com>
 */
public class JcsegAnalyzerProvider extends
        AbstractIndexAnalyzerProvider<JcsegAnalyzer4X> {

    private final JcsegAnalyzer4X analyzer;

    @Inject
    public JcsegAnalyzerProvider(Index index,
            @IndexSettings Settings indexSettings, Environment env,
            @Assisted String name, @Assisted Settings settings) {
        super(index, indexSettings, name, settings);
        // System.out.println("###Analyzer: "+env.configFile()+"###");
        File proFile = new File(env.configFile() + "/jcseg/jcseg.properties");
        String seg_mode = settings.get("seg_mode", "complex");

        int mode = JcsegTaskConfig.COMPLEX_MODE;
        if (seg_mode.equals("complex"))
            mode = JcsegTaskConfig.COMPLEX_MODE;
        else if (seg_mode.equals("simple"))
            mode = JcsegTaskConfig.SIMPLE_MODE;
        else if (seg_mode.equals("detect"))
            mode = JcsegTaskConfig.DETECT_MODE;

        if (proFile.exists())
            analyzer = new JcsegAnalyzer4X(mode, proFile.getPath());
        else
            analyzer = new JcsegAnalyzer4X(mode);
    }

    @Override
    public JcsegAnalyzer4X get() {
        return this.analyzer;
    }
}
