package org.lionsoul.jcseg.elasticsearch.index.analysis;

import java.io.FileInputStream;
import java.io.IOException;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.lionsoul.jcseg.analyzer.JcsegAnalyzer;
import org.lionsoul.jcseg.elasticsearch.util.CommonUtil;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;

/**
 * JcsegAnalyzerProvider
 * 
 * @author chenxin<chenxin619315@gmail.com>
 */
public abstract class JcsegAnalyzerProvider extends AbstractIndexAnalyzerProvider<JcsegAnalyzer>
{
    /**default Jcseg tokenizer instance*/
    private final JcsegAnalyzer analyzer;

    @Inject
    public JcsegAnalyzerProvider(
            IndexSettings indexSettings, Environment env, String name, Settings settings) throws IOException {
        super(indexSettings, name, settings);
        
        JcsegTaskConfig config = new JcsegTaskConfig(new FileInputStream(CommonUtil.getPluginSafeFile("jcseg.properties")));
        analyzer = new JcsegAnalyzer(this.getSegMode(), config);
    }

    /**
     * internal method to load the lexicon under the plugin directory
     *
     * @param   config
     * @param   dic
     */
//    protected void loadLexicon(JcsegTaskConfig config, ADictionary dic) throws IOException {
//        String[] lexPath = config.getLexiconPath();
//        if ( lexPath == null ) {
//            dic.loadClassPath();
//        } else {
//            for ( String path : lexPath ) {
//                final File safeDir = CommonUtil.getPluginSafeFile(path);
//                if ( ! safeDir.exists() ) {
//                    continue;
//                }
//
//                File[] files = safeDir.listFiles(new FilenameFilter(){
//                    @Override
//                    public boolean accept(File dir, String name) {
//                        return (name.startsWith("lex-") && name.endsWith(".lex"));
//                    }
//                });
//
//                for ( File f : files ) {
//                    System.out.println(f.getAbsolutePath());
//                    dic.load(CommonUtil.getPluginSafeFile(f.getAbsolutePath()));
//                }
//            }
//
//            if ( config.isAutoload() ) {
//                dic.startAutoload();
//            }
//        }
//
//        dic.resetSynonymsNet();
//    }
    
    protected abstract int getSegMode();
    
    @Override public JcsegAnalyzer get() 
    {
        return this.analyzer;
    }
    
}
