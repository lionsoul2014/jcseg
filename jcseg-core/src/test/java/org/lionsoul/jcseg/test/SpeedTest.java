package org.lionsoul.jcseg.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.lionsoul.jcseg.ISegment;
import org.lionsoul.jcseg.IWord;
import org.lionsoul.jcseg.dic.ADictionary;
import org.lionsoul.jcseg.dic.DictionaryFactory;
import org.lionsoul.jcseg.segmenter.SegmenterConfig;

/**
 * jcseg speed test program
 * 
 * @author    chenxin<chenxin619315@gmail.com>
 */
public class SpeedTest {
    
    public static ISegment seg = null;
    
    public static String segment(Reader reader, int type) throws IOException 
    {
        
        if ( seg == null ) 
        {
            long start = System.currentTimeMillis();
            SegmenterConfig  config = new SegmenterConfig(true);
            ADictionary dic = DictionaryFactory.createDefaultDictionary(config);
            //load lexicon
            //for ( String lpath : config.getLexiconPath() )
            //    dic.loadFromLexiconDirectory(lpath);
            seg = ISegment.Type.fromIndex(type).factory.create(config, dic);
            System.out.println("Diciontary Loaded, cost:"+
                    (System.currentTimeMillis() - start)+" msec");
        }
        
        StringBuilder sb = new StringBuilder();
        seg.reset(reader);
        //seg.setLastRule(null);
        IWord word = null;
        
        int counter = 0;
        long _start = System.currentTimeMillis();
        while ( (word = seg.next()) != null ) 
        {
            sb.append(word.getValue());
            sb.append("  ");
            counter++;
        }
        System.out.println("Done, cost:"+(System.currentTimeMillis() - _start)+" msec");
        System.out.println("总字数："+seg.getStreamPosition()+", split: "+counter);
        
        return sb.toString();
    }

    /**
     * @param args
     */
    public static void main(String[] args) 
    {
        String filename = "/java/products/jcseg_o/article/article";
        if (  args.length >= 1 ) 
            filename = args[0];
        try {
            segment(new StringReader("jcseg中文分词组件。"), ISegment.COMPLEX_MODE);
            segment(new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(filename), "UTF-8")),
                    ISegment.COMPLEX_MODE);
            //System.out.println("Complex-> "+segment(sb.toString(), Config.COMPLEX_MODE));
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
}
