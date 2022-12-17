package org.lionsoul.jcseg.test;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;

import org.lionsoul.jcseg.ISegment;
import org.lionsoul.jcseg.IWord;
import org.lionsoul.jcseg.dic.ADictionary;
import org.lionsoul.jcseg.dic.DictionaryFactory;
import org.lionsoul.jcseg.extractor.KeyphraseExtractor;
import org.lionsoul.jcseg.extractor.KeywordsExtractor;
import org.lionsoul.jcseg.extractor.SummaryExtractor;
import org.lionsoul.jcseg.extractor.impl.TextRankKeyphraseExtractor;
import org.lionsoul.jcseg.extractor.impl.TextRankKeywordsExtractor;
import org.lionsoul.jcseg.extractor.impl.TextRankSummaryExtractor;
import org.lionsoul.jcseg.segmenter.NLPSeg;
import org.lionsoul.jcseg.segmenter.SegmenterConfig;
import org.lionsoul.jcseg.sentence.SentenceSeg;
import org.lionsoul.jcseg.util.ArrayUtil;

/**
 * Jcseg test program.
 * 
 * @author  chenxin<chenxin619315@gmail.com>
 */
public class JcsegTest 
{
    final SegmenterConfig tokenizerConfig;
    final ADictionary dic;
    
    ISegment tokenizerSeg;
    ISegment extractorSeg;
    
    final KeywordsExtractor keywordsExtractor;
    final KeyphraseExtractor keyphraseExtractor;
    final SummaryExtractor summaryExtractor;
    
    public JcsegTest() throws IOException, CloneNotSupportedException 
    {
        tokenizerConfig = new SegmenterConfig(true);
        SegmenterConfig extractorConfig = tokenizerConfig.clone();
        ///reset the options from a property file.
        ///config.load("/java/JavaSE/jcseg/jcseg.properties");
        
        //ADictionary dic = DictionaryFactory.createDefaultDictionary(tokenizerConfig);
        dic = DictionaryFactory.createSingletonDictionary(tokenizerConfig);
        
        //two ways to reload lexicons
        ///for ( String lPath : config.getLexiconPath() )
        ///    dic.loadDirectory(lPath);
        ///dic.load("/java/lex-main.lex");
        tokenizerSeg = ISegment.COMPLEX.factory.create(tokenizerConfig, dic);
        
        //segmentation object for extractor
        extractorConfig.setAppendCJKPinyin(false);
        extractorConfig.setClearStopwords(true);
        extractorConfig.setKeepUnregWords(false);
        extractorSeg = ISegment.COMPLEX.factory.create(extractorConfig, dic);
        
        //create and initialize the extractor
        keywordsExtractor  = new TextRankKeywordsExtractor(tokenizerSeg);
        keyphraseExtractor = new TextRankKeyphraseExtractor(tokenizerSeg);
        summaryExtractor   = new TextRankSummaryExtractor(tokenizerSeg, new SentenceSeg());
        
        TextRankKeyphraseExtractor trkp = (TextRankKeyphraseExtractor)keyphraseExtractor;
        trkp.setAutoMinLength(4);
        trkp.setMaxWordsNum(4);
        
        //append pinyin
        System.out.println("Jcseg参数设置：");
        System.out.println("当加载的配置文件："+tokenizerConfig.getPropertieFile());
        /// System.out.println("最大切分匹配词数："+tokenizerConfig.MAX_LENGTH);
        /// System.out.println("开启中文人名识别："+tokenizerConfig.I_CN_NAME);
        /// System.out.println("最大姓氏前缀修饰："+tokenizerConfig.MAX_CN_LNADRON);
        /// System.out.println("最大标点配对词长："+tokenizerConfig.PPT_MAX_LENGTH);
        /// System.out.println("词库词条拼音加载："+tokenizerConfig.LOAD_CJK_PINYIN);
        /// System.out.println("分词词条拼音追加："+tokenizerConfig.APPEND_CJK_PINYIN);
        /// System.out.println("词库同义词的载入："+tokenizerConfig.LOAD_CJK_SYN);
        /// System.out.println("分词同义词的追加："+tokenizerConfig.APPEND_CJK_SYN);
        /// System.out.println("词库词条词性载入："+tokenizerConfig.LOAD_CJK_POS);
        /// System.out.println("去除切分后噪音词："+tokenizerConfig.CLEAR_STOPWORD);
        /// System.out.println("中文数字转阿拉伯："+tokenizerConfig.CNNUM_TO_ARABIC);
        /// System.out.println("中文分数转阿拉伯："+tokenizerConfig.CNFRA_TO_ARABIC);
        /// System.out.println("保留未识别的字符："+tokenizerConfig.KEEP_UNREG_WORDS);
        /// System.out.println("英文词条二次切分："+tokenizerConfig.EN_SECOND_SEG);
        /// System.out.println("姓名成词歧义阕值："+tokenizerConfig.NAME_SINGLE_THRESHOLD+"\n");
    }
    
    /**
     * string tokenize handler
     * 
     * @param str
    */
    public void tokenize(String str) throws IOException 
    {
        final StringBuilder sb = new StringBuilder();
        /// seg.setLastRule(null);
        IWord word = null/*, lastWord = null*/;
        
        long _start = System.nanoTime();
        boolean isFirst = true, entity = (tokenizerSeg instanceof NLPSeg);
        int counter = 0;
        tokenizerSeg.reset(new StringReader(str));
        while ( (word = tokenizerSeg.next()) != null ) {
            if ( isFirst ) {
                sb.append(word.getValue());
                isFirst = false;
            } else {
                sb.append(" ");
                sb.append(word.getValue());
            }
            
            //----for testing append word position and length
            sb.append("[");
            sb.append(word.getPosition());
            sb.append(",");
            sb.append(word.getLength());
            sb.append("]");
            
            // append the part of the speech
            if ( word.getPartSpeech() != null ) {
                sb.append('/');
                sb.append(word.getPartSpeech()[0]);
            }
            
            // check and append the entity recognition
            if ( entity ) {
                sb.append('/');
                sb.append(ArrayUtil.implode("|", word.getEntity()));
            }
            
            // check the word offset and position
            /// if ( lastWord == null ) {
            ///     lastWord = word;
            /// } else {
            ///     if ( word.getPosition() < lastWord.getPosition() ) {
            ///         sb.append("/PositionError:["+word.getPosition()+","+lastWord.getPosition()+"]");
            ///     } else if ( word.getPosition() + word.getLength() <
            ///             lastWord.getPosition() + lastWord.getLength() ) {
            ///         sb.append("/OffsetError:["+(word.getPosition()+word.getLength())+
            ///                 ", "+(lastWord.getPosition()+lastWord.getLength())+"]");
            ///     }
            ///
            ///     lastWord = word;
            /// }

            /// if ( word.getPosition() < 0 ) {
            ///     System.out.println("Negative position: " + word);
            /// } else if ( lastWord == null  ) {
            ///     lastWord = word;
            /// } else if ( word.getPosition() < lastWord.getPosition() ) {
            ///     System.out.println("Word position go backwards: " + word);
            ///     lastWord = word;
            /// } else if ( word.getPosition() > word.getPosition() + word.getLength() ) {
            ///     lastWord = word;
            ///     System.out.println("startOffset > endOffset" + word);
            /// }
            
            // clear the allocations of the word.
            word = null;
            counter++;
        }
        
        long e = System.nanoTime();
        System.out.println("分词结果：");
        System.out.println(sb.toString());
        System.out.format("Done, total:"
                + tokenizerSeg.getStreamPosition() + ", tokens:" +
                + counter + ", in %.5fsec\n", ((float)e - _start)/1E9);
    }
    
    public void resetMode(String name)
    {
    	tokenizerSeg = ISegment.Type.fromString(name).factory.create(tokenizerConfig, dic);
    }
    
    /**
     * keywords extractor
     * 
     * @param   str
    */
    public void keywords(String str) throws IOException
    {
        long _start = System.nanoTime();
        final List<String> keywords = keywordsExtractor.getKeywordsFromString(str);
        long e = System.nanoTime();
        System.out.println("Top10关键词：");
        System.out.println(keywords);
        System.out.format("Done in %.5fsec\n", ((float)e - _start)/1E9);
    }
    
    /**
     * keyphrase extractor
     * 
     * @param   str
    */
    public void keyphrase(String str) throws IOException
    {
        long _start = System.nanoTime();
        final List<String> keyphrase = keyphraseExtractor.getKeyphraseFromString(str);
        long e = System.nanoTime();
        System.out.println("Top10关键短语：");
        System.out.println(keyphrase);
        System.out.format("Done in %.5fsec\n", ((float)e - _start)/1E9);
    }
    
    /**
     * key sentence extractor
     * 
     * @param   str
    */
    public void sentence(String str) throws IOException
    {
        long _start = System.nanoTime();
        final List<String> sentence = summaryExtractor.getKeySentenceFromString(str);
        long e = System.nanoTime();
        System.out.println("Top6相关句子：");
        //System.out.println(sentence);
        System.out.println("+-Key sentence: ");
        int count = 1;
        for ( String sen : sentence ) {
            System.out.println(count + ": " + sen);
            count++;
        }
        System.out.format("Done in %.5fsec\n", ((float)e - _start)/1E9);
    }
    
    /**
     * summary extractor
     * 
     * @param   str
    */
    public void summary(String str) throws IOException
    {
        long _start = System.nanoTime();
        final String summary = summaryExtractor.getSummaryFromString(str, 86);
        long e = System.nanoTime();
        System.out.println("摘要结果：");
        System.out.println(summary);
        System.out.format("Done in %.5fsec\n", ((float)e - _start)/1E9);
    }

    public static void main(String[] args) throws IOException, CloneNotSupportedException
    {
        String str = "歧义和同义词:研究生命起源，" +
                "混合词: 做B超检查身体，x射线本质是什么，今天去奇都ktv唱卡拉ok去，哆啦a梦是一个动漫中的主角，" +
                "单位和全角: 2009年８月６日开始大学之旅，岳阳今天的气温为38.6℃, 也就是101.48℉, " +
                "中文数字/分数: 你分三十分之二, 小陈拿三十分之五,剩下的三十分之二十三全部是我的，那是一九九八年前的事了，四川麻辣烫很好吃，五四运动留下的五四精神。笔记本五折包邮亏本大甩卖。"+
                "人名识别: 我是陈鑫，也是jcseg的作者，三国时期的诸葛亮是个天才，我们一起给刘翔加油，罗志高兴奋极了因为老吴送了他一台笔记本。" +
                "冰岛时间7月1日，正在当地拍片的汤姆·克鲁斯通过发言人承认，他与第三任妻子凯蒂·赫尔墨斯（第一二任妻子分别为咪咪·罗杰斯、妮可·基德曼）的婚姻即将结束。" + 
                "配对标点: 本次『畅想杯』黑客技术大赛的得主为电信09-2BF的张三，奖励C++程序设计语言一书和【畅想网络】的『PHP教程』一套。"+
                "特殊字母: 【Ⅰ】（Ⅱ），" +
                "英文数字: bug report chenxin619315@gmail.com or visit https://code.google.com/p/jcseg, we all admire the hacker spirit!" +
                "特殊数字: ① ⑩ ⑽ ㈩.";
        ///str = "这是张三和李四一九九七年前的故事了。";
        ///str = "本次“畅想杯黑客技术大赛”的冠军为“电信09-2BF”的陈鑫。奖励《算法导论》一书，加上『畅想网络PHP教程』一套";
        ///str = "我很喜欢陈述高的演讲。我很不喜欢陈述高调的样子。";
        ///str = "学习宣传林俊德同志的先进事迹";
        ///str = "每年的五四青年节都让我们想起了过去的五四运动，《Java编程思想》五折亏本卖。";
        ///str = "c++编程思想,c#是.net平台的主要开发语言,b超。";
        ///str = "关于这个软件的盈利我们六四分之也就是我拿十分之六剩下的给你, 你觉得怎么样?";
        
        int action = 0;
        String cmd = null, module = "tokenizer:complex";
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        JcsegTest demo = new JcsegTest();
        System.out.println(str);
        try {
            demo.tokenize(str);
            printHelpMenu();
            do {
                System.out.print("jcseg~" + module +">> ");
                cmd = reader.readLine();
                if ( cmd == null ) break;
                cmd = cmd.trim();
                if ( "".equals(cmd) ) continue;
                
                //module switch
                if ( cmd.charAt(0) == ':' ) {
                    switch (cmd) {
                    case ":complex":
                        demo.resetMode("complex");
                        module = "tokenizer:complex";
                        action = 0;
                        System.out.println("Entered complex tokenize mode!");
                        continue;
                    case ":simple":
                        demo.resetMode("simple");
                        module = "tokenizer:simple";
                        action = 0;
                        System.out.println("Entered simple tokenize mode!");
                        continue;
                    case ":most":
                        demo.resetMode("most");
                        module = "tokenizer:most";
                        action = 0;
                        System.out.println("Entered most tokenize mode!");
                        continue;
                    case ":detect":
                        demo.resetMode("detect");
                        module = "tokenizer:detect";
                        action = 0;
                        System.out.println("Entered detect tokenize mode!");
                        continue;
                    case ":delimiter":
                        demo.resetMode("delimiter");
                        module = "tokenizer:delimiter";
                        action = 0;
                        System.out.println("Entered delimiter tokenize mode!");
                        continue;
                    case ":NLP":
                        demo.resetMode("nlp");
                        module = "tokenizer:NLP";
                        action = 0;
                        System.out.println("Entered NLP tokenize mode!");
                        continue;
                    case ":ngram":
                        demo.resetMode("ngram");
                        module = "tokenizer:ngram";
                        action = 0;
                        System.out.println("Entered ngram tokenize mode!");
                        continue;
                    case ":keywords":
                        module = "keywords";
                        action = 1;
                        System.out.println("Entered keywords extract mode!");
                        continue;
                    case ":keyphrase":
                        module = "keyphrase";
                        action = 2;
                        System.out.println("Entered keyphrase extract mode!");
                        continue;
                    case ":sentence":
                        action = 3;
                        module = "sentence";
                        System.out.println("Entered sentence extract mode!");
                        continue;
                    case ":summary":
                        action = 4;
                        module = "summary";
                        System.out.println("Entered summary extract mode!");
                        continue;
                    case ":help":
                        printHelpMenu();
                        continue;
                    case ":quit":
                        System.out.println("Thanks for trying Jcseg, Bye!");
                        System.exit(0);
                    }
                }
            
                //segment
                switch ( action )
                {
                case 0: demo.tokenize(cmd); break;
                case 1: demo.keywords(cmd); break;
                case 2: demo.keyphrase(cmd);break;
                case 3: demo.sentence(cmd); break;
                case 4: demo.summary(cmd);  break;
                }
            } while ( true );
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println("Bye!");
    }
    
    static void printHelpMenu()
    {
        System.out.println("+--------Jcseg chinese word tokenizer demo-------------------+");
        System.out.println("|- @Author chenxin<chenxin619315@gmail.com>                  |");
        System.out.println("|- :seg_mode  : switch to specified tokenizer mode.          |");
        System.out.println("|- (:complex,:simple,:most,:detect,:delimiter,:NLP,:ngram)   |");
        System.out.println("|- :keywords  : switch to keywords extract mode.             |");
        System.out.println("|- :keyphrase : switch to keyphrase extract mode.            |");
        System.out.println("|- :sentence  : switch to sentence extract mode.             |");
        System.out.println("|- :summary   : switch to summary extract mode.              |");
        System.out.println("|- :help      : print this help menu.                        |");
        System.out.println("|- :quit      : to exit the program.                         |");
        System.out.println("+------------------------------------------------------------+");
    }
}
