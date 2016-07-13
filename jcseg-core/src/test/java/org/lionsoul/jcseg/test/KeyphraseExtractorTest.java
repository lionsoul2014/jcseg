package org.lionsoul.jcseg.test;

import java.io.IOException;
import java.util.List;

import org.lionsoul.jcseg.extractor.impl.TextRankKeyphraseExtractor;
import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.DictionaryFactory;
import org.lionsoul.jcseg.tokenizer.core.ISegment;
import org.lionsoul.jcseg.tokenizer.core.JcsegException;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;
import org.lionsoul.jcseg.tokenizer.core.SegmentFactory;

public class KeyphraseExtractorTest {

    public static void main(String[] args) 
    {
        //create your JcsegTaskConfig here please
        JcsegTaskConfig config = new JcsegTaskConfig(true); 
        //config.setClearStopwords(true);
        config.setAppendCJKSyn(false);
        config.setEnSecondSeg(false);
        config.setKeepUnregWords(false);
        ADictionary dic = DictionaryFactory.createSingletonDictionary(config);
        
        try {
            ISegment seg = SegmentFactory
                    .createJcseg(JcsegTaskConfig.COMPLEX_MODE, new Object[]{config, dic});
            
            TextRankKeyphraseExtractor extractor = new TextRankKeyphraseExtractor(seg);
            extractor.setMaxIterateNum(100);
            extractor.setWindowSize(5);
            extractor.setKeywordsNum(15);
            extractor.setMaxWordsNum(4);
            //extractor.setAutoMinLength(4);
            
            List<String> phrases;
            phrases = extractor.getKeyphraseFromString("支持向量机广泛应用于文本挖掘，例如，"
                    + "基于支持向量机的文本自动分类技术研究一文中很详细的介绍支持向量机的算法细节，"
                    + "文本自动分类是文本挖掘技术中的一种！");
            //phrases = extractor.getKeyphraseFromFile("/home/chenxin/curpos/5.txt");
            System.out.println(phrases);
        } catch (JcsegException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
