package com.webssky.jcseg.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import com.webssky.jcseg.core.ADictionary;
import com.webssky.jcseg.core.DictionaryFactory;
import com.webssky.jcseg.core.ISegment;
import com.webssky.jcseg.core.IWord;
import com.webssky.jcseg.core.JcsegException;
import com.webssky.jcseg.core.JcsegTaskConfig;
import com.webssky.jcseg.core.SegmentFactory;

/**
 * jcseg speed test program . <br />
 * 
 * @author	chenxin<chenxin619315@gmail.com>
 */
public class SpeedTest {
	
	public static ISegment seg = null;
	
	public static String segment(Reader reader, int type) throws JcsegException, IOException {
		
		if ( seg == null ) {
			long start = System.currentTimeMillis();
			JcsegTaskConfig  config = new JcsegTaskConfig();
			ADictionary dic = DictionaryFactory.createDefaultDictionary(config);
			dic.loadFromLexiconDirectory(config.getLexiconPath());
			seg = SegmentFactory.createJcseg(JcsegTaskConfig.COMPLEX_MODE, 
					new Object[]{config, dic});
			System.out.println("Diciontary Loaded, cost:"+(System.currentTimeMillis() - start)+" msec");
		}
		
		StringBuffer sb = new StringBuffer();
		seg.reset(reader);
		//seg.setLastRule(null);
		IWord word = null;
		
		int counter = 0;
		long _start = System.currentTimeMillis();
		while ( (word = seg.next()) != null ) {
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
	public static void main(String[] args) {
		String filename = "/java/products/jcseg_o/article/article";
		if (  args.length >= 1 ) 
			filename = args[0];
		try {
			segment(new StringReader("jcseg中文分词组件。"), JcsegTaskConfig.COMPLEX_MODE);
			segment(new BufferedReader(
					new InputStreamReader(
							new FileInputStream(filename), "UTF-8")),
					JcsegTaskConfig.COMPLEX_MODE);
			//System.out.println("Complex-> "+segment(sb.toString(), Config.COMPLEX_MODE));
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
