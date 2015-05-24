package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.settings.IndexSettings;
import org.lionsoul.jcseg.core.ADictionary;
import org.lionsoul.jcseg.core.DictionaryFactory;
import org.lionsoul.jcseg.core.JcsegException;
import org.lionsoul.jcseg.core.JcsegTaskConfig;
import org.lionsoul.jcseg.analyzer.JcsegTokenizer;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

/**
 * JcsegTokenizerFactory
 * 
 * @author chenxin<chenxin6193152gmail.com>
 */
public class JcsegTokenizerFactory extends AbstractTokenizerFactory {

    private String seg_mode;
    private JcsegTaskConfig config;
    private ADictionary dic;

    @Inject
    public JcsegTokenizerFactory(Index index,
            @IndexSettings Settings indexSettings, Environment env,
            @Assisted String name, @Assisted Settings settings) {
        super(index, indexSettings, name, settings);

        File proFile = new File(env.configFile() + "/jcseg/jcseg.properties");
        seg_mode = settings.get("seg_mode", "complex");

        if (proFile.exists())
            config = new JcsegTaskConfig(proFile.getPath());
        else
            config = new JcsegTaskConfig();

        dic = DictionaryFactory.createDefaultDictionary(config);
    }

    @Override
    public Tokenizer create(Reader reader) {
        int mode = JcsegTaskConfig.COMPLEX_MODE;
        if (seg_mode.equals("complex"))
            mode = JcsegTaskConfig.COMPLEX_MODE;
        else if (seg_mode.equals("simple"))
            mode = JcsegTaskConfig.SIMPLE_MODE;
        else if (seg_mode.equals("detect"))
            mode = JcsegTaskConfig.DETECT_MODE;

        try {
            return new JcsegTokenizer(reader, mode, config, dic);
        } catch (JcsegException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }
}
