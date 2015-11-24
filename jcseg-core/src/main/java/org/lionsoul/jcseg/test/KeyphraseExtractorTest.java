package org.lionsoul.jcseg.test;

import java.io.IOException;
import java.util.List;

import org.lionsoul.jcseg.core.ADictionary;
import org.lionsoul.jcseg.core.DictionaryFactory;
import org.lionsoul.jcseg.core.ISegment;
import org.lionsoul.jcseg.core.JcsegException;
import org.lionsoul.jcseg.core.JcsegTaskConfig;
import org.lionsoul.jcseg.core.SegmentFactory;
import org.lionsoul.jcseg.nlp.impl.TextRankKeyphraseExtractor;

public class KeyphraseExtractorTest {

	public static void main(String[] args) 
	{
		//create your JcsegTaskConfig here please
		JcsegTaskConfig config = new JcsegTaskConfig("/java/test/jcseg.properties"); 
		
		//config.setClearStopwords(true);
		config.setAppendCJKSyn(false);
		config.setEnSecondSeg(false);
		ADictionary dic = DictionaryFactory.createDefaultDictionary(config);
		
		try {
			ISegment seg = SegmentFactory
					.createJcseg(JcsegTaskConfig.COMPLEX_MODE, new Object[]{config, dic});
			
			TextRankKeyphraseExtractor extractor = new TextRankKeyphraseExtractor(seg);
			extractor.setMaxIterateNum(100);
			extractor.setWindowSize(5);
			extractor.setKeywordsNum(15);
			extractor.setMaxPhraseLength(7);
			//extractor.setAutoMinLength(4);
			
			List<String> phrases;
			phrases = extractor.getKeyphraseFromFile("/home/chenxin/curpos/7.txt");
			System.out.println(phrases);
		} catch (JcsegException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
