package org.lionsoul.jcseg.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.lionsoul.jcseg.core.ADictionary;
import org.lionsoul.jcseg.core.DictionaryFactory;
import org.lionsoul.jcseg.core.ISegment;
import org.lionsoul.jcseg.core.IWord;
import org.lionsoul.jcseg.core.JcsegException;
import org.lionsoul.jcseg.core.JcsegTaskConfig;
import org.lionsoul.jcseg.core.SegmentFactory;


/**
 * jcseg test program.
 * 
 * @author	chenxin<chenxin619315@gmail.com>
 */
public class JcsegTest {
	
	ISegment seg = null;
	
	public JcsegTest() throws JcsegException, IOException {
		
		JcsegTaskConfig config = new JcsegTaskConfig();
		//JcsegTaskConfig config = new JcsegTaskConfig("/java/JavaSE/jcseg/jcseg.properties"); 
		//JcsegTaskConfig config = new JcsegTaskConfig(null);
		//reset the options from a property file.
		//config.resetFromPropertyFile("/java/JavaSE/jcseg/jcseg.properties");
		
		ADictionary dic = DictionaryFactory.createDefaultDictionary(config);
		
		//two ways to reload lexicons
		//for ( String lpath : config.getLexiconPath() )
		//	dic.loadFromLexiconDirectory(lpath);
		//dic.loadFromLexiconFile("/java/lex-main.lex");
		seg = SegmentFactory
				.createJcseg(JcsegTaskConfig.COMPLEX_MODE, new Object[]{config, dic});
		
		//append pinyin
		//config.setAppendCJKPinyin(true);
		System.out.println("jcseg参数设置：");
		System.out.println("最大切分匹配词数："+config.MAX_LENGTH);
		System.out.println("最大混合中文长度："+config.MIX_CN_LENGTH);
		System.out.println("开启中文人名识别："+config.I_CN_NAME);
		System.out.println("最大姓氏前缀修饰："+config.MAX_CN_LNADRON);
		System.out.println("最大标点配对词长："+config.PPT_MAX_LENGTH);
		System.out.println("词库词条拼音加载："+config.LOAD_CJK_PINYIN);
		System.out.println("分词词条拼音追加："+config.APPEND_CJK_PINYIN);
		System.out.println("词库同义词的载入："+config.LOAD_CJK_SYN);
		System.out.println("分词同义词的追加："+config.APPEND_CJK_SYN);
		System.out.println("词库词条词性载入："+config.LOAD_CJK_POS);
		System.out.println("去除切分后噪音词："+config.CLEAR_STOPWORD);
		System.out.println("中文数字转阿拉伯："+config.CNNUM_TO_ARABIC);
		System.out.println("中文分数转阿拉伯："+config.CNFRA_TO_ARABIC);
		System.out.println("保留未识别的字符："+config.KEEP_UNREG_WORDS);
		System.out.println("英文词条二次切分："+config.EN_SECOND_SEG);
		System.out.println("姓名成词歧义阕值："+config.NAME_SINGLE_THRESHOLD+"\n");
	}
	
	public void segment(String str) throws IOException {
		
		StringBuffer sb = new StringBuffer();
		//seg.setLastRule(null);
		IWord word = null;
		
		long _start = System.nanoTime();
		boolean isFirst = true;
		int counter = 0;
		seg.reset(new StringReader(str));
		while ( (word = seg.next()) != null ) {
			if ( isFirst ) {
				sb.append(word.getValue());
				isFirst = false;
			}
			else {
				sb.append(" ");
				sb.append(word.getValue());
			}
			//append the part of the speech
			if ( word.getPartSpeech() != null ) {
				sb.append('/');
				sb.append(word.getPartSpeech()[0]);
			}
			//clear the allocations of the word.
			word = null;
			counter++;
		}
		long e = System.nanoTime();
		System.out.println("分词结果：");
		System.out.println(sb.toString());
		System.out.format("Done, total:"
				+seg.getStreamPosition()+", split:" +
				+counter+", cost: %.5fsec(less than)\n", ((float)e - _start)/1E9);
	}

	/**
	 * @param args
	 * @throws JcsegException 
	 * @throws IOException
	 */
	public static void main(String[] args) throws JcsegException, IOException {
		String str = "歧义和同义词:研究生命起源，" +
				"混合词: 做B超检查身体，x射线本质是什么，今天去奇都ktv唱卡拉ok去，哆啦a梦是一个动漫中的主角，" +
				"单位和全角: 2009年８月６日开始大学之旅，岳阳今天的气温为38.6℃, 也就是101.48℉, " +
				"中文数字/分数: 你分三十分之二, 小陈拿三十分之五,剩下的三十分之二十三全部是我的，那是一九九八年前的事了，四川麻辣烫很好吃，五四运动留下的五四精神。笔记本五折包邮亏本大甩卖。"+
				"人名识别: 我是陈鑫，也是jcesg的作者，三国时期的诸葛亮是个天才，我们一起给刘翔加油，" +
						"罗志高兴奋极了因为老吴送了他一台笔记本。" +
				"配对标点: 本次『畅想杯』黑客技术大赛的得主为电信09-2BF的张三，奖励C++程序设计语言一书和【畅想网络】的『PHP教程』一套。"+
				"特殊字母: 【Ⅰ】（Ⅱ），" +
				"英文数字: bug report chenxin619315@gmail.com or visit http://code.google.com/p/jcseg, we all admire the hacker spirit!" +
				"特殊数字: ① ⑩ ⑽ ㈩.";
		//str = "这是张三和李四一九九七年前的故事了。";
		//str = "本次“畅想杯黑客技术大赛”的冠军为“电信09-2BF”的陈鑫。奖励《算法导论》一书，加上『畅想网络PHP教程』一套";
		//str = "我很喜欢陈述高的演讲。我很不喜欢陈述高调的样子。";
		//str = "学习宣传林俊德同志的先进事迹";
		//str = "每年的五四青年节都让我们想起了过去的五四运动，《Java编程思想》五折亏本卖。";
		//str = "c++编程思想,c#是.net平台的主要开发语言,b超。";
		//str = "关于这个软件的盈利我们六四分之也就是我拿十分之六剩下的给你, 你觉得怎么样?";
		
		String cmd = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		JcsegTest demo = new JcsegTest();
		System.out.println(str);
		try {
			demo.segment(str);
			System.out.println("+--------jcseg chinese word segment demo-------+");
			System.out.println("|- Suggest email: chenxin619315@gmail.com      |");
			System.out.println("|- Run quit or exit to exit.                   |");
			System.out.println("+----------------------------------------------+");
			do {
				System.out.print("jcseg>> ");
				cmd = reader.readLine();
				if ( cmd == null ) break;
				cmd = cmd.trim();
				if ( "".equals(cmd) ) continue;
				if ( cmd.equals("quit") || cmd.equals("exit")) {
					System.out.println("Thanks for trying jcseg, Bye!");
					System.exit(0);
				}
			
				//segment
				demo.segment(cmd);
			} while ( true );
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Bye!");
	}

}
