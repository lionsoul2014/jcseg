# Jcseg是什么？

Jcseg是基于mmseg算法的一个轻量级开源中文分词器，同时集成了关键字提取，关键短语提取，关键句子提取，和自动摘要等功能，并且提供了最高版本的lucene, solr, elasticsearch的分词接口，Jcseg自带了一个jcseg.properties文件，快速编辑配置得到适合不同场合的分词应用，例如：最大匹配词长，是否开启中文人名识别，是否追加拼音，是否追加同义词等！

# Jcseg核心功能？

* 1，中文分词：mmseg算法+Jcseg自带的优化算法。

* 2，关键字提取/关键短语：基于textRank算法。

* 3，关键句子提取：基于textRank算法。

* 4，文章自动摘要：基于BM25+textRank算法。

* 5，自动词性标注：目前只是基于词库，效果不是很理想。

* 5，restful api：嵌入jetty提供了一个绝对高性能的server模块，包含全部功能的http接口，方便各种语言客户端调用。

# Jcseg中文分词特色：

1. 三种切分模式：

        (1).简易模式：FMM算法，适合速度要求场合。
        (2).复杂模式-MMSEG四种过滤算法，具有较高的歧义去除，分词准确率达到了98.41%。
        (3).检测模式：只返回词库中已有的词条，很适合某些应用场合。(1.9.4版本开始)

2. 支持自定义词库。在lexicon文件夹下，可以随便添加/删除/更改词库和词库内容，并且对词库进行了分类。参考下面了解如何给jcseg添加词库/新词。

3. 支持词库多目录加载. 配置lexicon.path中使用';'隔开多个词库目录.

4. 词库分为简体/繁体/简繁体混合词库: 可以专门适用于简体切分, 繁体切分, 简繁体混合切分, 并且可以利用下面提到的同义词实现,简繁体的相互检索,  jcseg同时提供了词库两个简单的词库管理工具来进行简繁体的转换和词库的合并.

5. 中英文同义词追加/同义词匹配 + 中文词条拼音追加．词库整合了《现代汉语词典》和cc-cedict辞典中的词条，并且依据cc-cedict词典为词条标上了拼音，依据《中华同义词词典》为词条标上了同义词(尚未完成)。更改jcseg.properties配置文档可以在分词的时候加入拼音和同义词到分词结果中。

6. 中文数字和中文分数识别，例如："一百五十个人都来了，四十分之一的人。"中的"一百五十"和"四十分之一"。并且jcseg会自动将其转换为阿拉伯数字加入到分词结果中。如：150， 1/40。

7. 支持中英混合词和英中混合词的识别(维护词库可以识别任何一种组合)。例如：B超, x射线, 卡拉ok, 奇都ktv, 哆啦a梦。

8. 更好的英文支持，电子邮件，域名，小数，分数，百分数，字母和标点组合词（例如C++, c#）的识别。

9. 自定义切分保留标点. 例如: 保留&, 就可以识别k&r这种复杂词条。

10. 复杂英文切分结果的二次切分:  可以保留原组合，同时可以避免复杂切分带来的检索命中率下降的情况，例如QQ2013会被切分成: qq2013/ qq/ 2013,  chenxin619315@gmail.com会被切分成: chenxin619315@gmail.com/ chenxin/ 619315/ gmail/ com。

11. 支持阿拉伯数字/小数/中文数字基本单字单位的识别，例如2012年，1.75米，38.6℃，五折，并且jcseg会将其转换为“5折”加入分词结果中。

12. 智能圆角半角, 英文大小写转换。

13. 特殊字母识别：例如：Ⅰ，Ⅱ；特殊数字识别：例如：①，⑩

14. 配对标点内容提取：例如：最好的Java书《java编程思想》，‘畅想杯黑客技术大赛’，被《,‘,“,『标点标记的内容。(1.6.8版开始支持)。

15. 智能中文人名/外文翻译人名识别。中文人名识别正确率达94%以上。（中文人名可以维护lex-lname.lex，lex-dname-1.lex，lex-dname-2.lex来提高准确率），(引入规则和词性后会达到98%以上的识别正确率)。

16. 自动中英文停止词过滤功能（需要在jcseg.properties中开启该选项，lex-stopwords.lex为停止词词库）。

17. 词库更新自动加载功能, 开启一个守护线程定时的检测词库的更新并且加载。

18. 自动词性标注（目前基于词库）。

# Jcseg快速体验：

    java -jar jcseg-core-{version}.jar
    //你将看到如下的终端界面：
    +--------Jcseg chinese word tokenizer demo---------+
    |- @Author chenxin<chenxin619315@gmail.com>        |
    |- :tokenizer : switch to tokenizer mode.          |
    |- :keywords  : switch to keywords extract mode.   |
    |- :keyphrase : switch to keyphrase extract mode.  |
    |- :sentence  : switch to sentence extract mode.   |
    |- :summary   : switch to summary extract mode.    |
    |- :help      : print this help menu.              |
    |- :quit      : to exit the program.               |
    +--------------------------------------------------+
    jcseg~tokenizer>> //在此输入文本开始测试

# Jcseg分词demo：

## 测试文本: 
    歧义和同义词:研究生命起源，混合词: 做B超检查身体，x射线本质是什么，今天去奇都ktv唱卡拉ok去，哆啦a梦是一个动漫中的主角，单位和全角: 2009年８月６日开始大学之旅，岳阳今天的气温为38.6℃, 也就是101.48℉, 中文数字/分数: 你分三十分之二, 小陈拿三十分之五,剩下的三十分之二十三全部是我的，那是一九九八年前的事了，四川麻辣烫很好吃，五四运动留下的五四精神。笔记本五折包邮亏本大甩卖。人名识别: 我是陈鑫，也是jcseg的作者，三国时期的诸葛亮是个天才，我们一起给刘翔加油，罗志高兴奋极了因为老吴送了他一台笔记本。外文名识别：冰岛时间7月1日，正在当地拍片的汤姆·克鲁斯通过发言人承认，他与第三任妻子凯蒂·赫尔墨斯（第一二任妻子分别为咪咪·罗杰斯、妮可·基德曼）的婚姻即将结束。配对标点: 本次『畅想杯』黑客技术大赛的得主为电信09-2BF的张三，奖励C++程序设计语言一书和【畅想网络】的『PHP教程』一套。特殊字母: 【Ⅰ】（Ⅱ），英文数字: bug report chenxin619315@gmail.com or visit http://code.google.com/p/jcseg, we all admire the hacker spirit!特殊数字: ① ⑩ ⑽ ㈩.

## 分词结果:
    歧义/n 和/o 同义词/n :/w 研究/vn 琢磨/vn 研讨/vn 钻研/vn 生命/n 起源/n ，/w 混合词 :/w 做/v b超/n 检查/vn 身体/n ，/w x射线/n x光线/n 本质/n 是/a 什么/n ，/w 今天/t 去/q 奇都ktv/nz 唱/n 卡拉ok/nz 去/q ，/w 哆啦a梦/nz 是/a 一个/q 动漫/n 中/q 的/u 主角/n ，/w 单位/n 和/o 全角/nz :/w 2009年/m 8月/m 6日/m 开始/n 大学/n 之旅 ，/w 岳阳/ns 今天/t 的/u 气温/n 为/u 38.6℃/m ,/w 也就是/v 101.48℉/m ,/w 中文/n 国语/n 数字/n //w 分数/n :/w 你/r 分/h 三十分之二/m ,/w 小陈/nr 拿/nh 三十分之五/m ,/w 剩下/v 的/u 三十分之二十三/m 全部/a 是/a 我的/nt ，/w 那是/c 一九九八年/m 1998年/m 前/v 的/u 事/i 了/i ，/w 四川/ns 麻辣烫/n 很/m 好吃/v ，/w 五四运动/nz 留下/v 的/u 五四/m 54/m 精神/n 。/w 笔记本/n 五折/m 5折/m 包邮 亏本/v 大甩卖 甩卖 。/w 人名/n 识别/v :/w 我/r 是/a 陈鑫/nr ，/w 也/e 是/a jcseg/en 的/u 作者/n ，/w 三国/mq 时期/n 的/u 诸葛亮/nr 是个 天才/n ，/w 我们/r 一起/d 给/v 刘翔/nr 加油/v ，/w 罗志高/nr 兴奋/v 极了/u 因为/c 老吴/nr 送了 他/r 一台 笔记本/n 。/w 外文/n 名/j 识别/v ：/w 冰岛/ns 时间/n 7月/m 1日/m ，/w 正在/u 当地/s 拍片/vi 的/u 汤姆·克鲁斯/nr 阿汤哥/nr 通过/v 发言人/n 承认/v ，/w 他/r 与/u 第三/m 任/q 妻子/n 凯蒂·赫尔墨斯/nr （/w 第一/a 二/j 任/q 妻子/n 分别为 咪咪·罗杰斯/nr 、/w 妮可·基德曼/nr ）/w 的/u 婚姻/n 即将/d 结束/v 。/w 配对/v 标点/n :/w 本次/r 『/w 畅想杯/nz 』/w 黑客/n 技术/n 大赛/vn 的/u 得主/n 为/u 电信/nt 09/en -/w bf/en 2bf/en 的/u 张三/nr ，/w 奖励/vn c++/en 程序设计/gi 语言/n 一书/ns 和/o 【/w 畅想网络/nz 】/w 的/u 『/w PHP教程/nz 』/w 一套/m 。/w 特殊/a 字母/n :/w 【/w Ⅰ/nz 】/w （/w Ⅱ/m ）/w ，/w 英文/n 英语/n 数字/n :/w bug/en report/en chenxin/en 619315/en gmail/en com/en chenxin619315@gmail.com/en or/en visit/en http/en :/w //w //w code/en google/en com/en code.google.com/en //w p/en //w jcseg/en ,/w we/en all/en admire/en appreciate/en like/en love/en enjoy/en the/en hacker/en spirit/en mind/en !/w 特殊/a 数字/n :/w ①/m ⑩/m ⑽/m ㈩/m ./w

# lucene集成Jcseg：

1. 导入jcseg-core-{version}.jar和jcseg-analyzer-{version}.jar
2. demo代码：

    Analyzer analyzer = new JcsegAnalyzer4X(JcsegTaskConfig.COMPLEX_MODE);
    //lucene 5.x版本
    //Analyzer analyzer = new JcsegAnalyzer5X(JcsegTaskConfig.COMPLEX_MODE);
    //非必须(用于修改默认配置): 获取分词任务配置实例
    JcsegAnalyzer4X jcseg = (JcsegAnalyzer4X) analyzer;
    JcsegTaskConfig config = jcseg.getTaskConfig();
    //追加同义词, 需要在 jcseg.properties 中配置 jcseg.loadsyn=1
    config. setAppendCJKSyn (true);
    //追加拼音, 需要在 jcseg.properties 中配置    jcseg.loadpinyin=1
    config. setAppendCJKPinyin ();
    //更多配置, 请查看 org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig
    
# solr集成Jcseg：

1. 将jcseg-core-{version}.jar和jcseg-analyzer-{version}.jar 复制到solr 的类库目录中。
2. 在solr的scheme.xml加入如下两种配置之一：

    <!----复杂模式分词: -->
    <fieldtype name="textComplex" class="solr.TextField">
        <analyzer>
            <tokenizer class="org.lionsoul.jcseg.analyzer.4x.JcsegTokenizerFactory"
        mode="complex"/>
        </analyzer>
    </fieldtype>
    <!----简易模式分词: -->
    <fieldtype name="textSimple" class="solr.TextField">
        <analyzer>
            <tokenizer class="org.lionsoul.jcseg.analyzer.4x.JcsegTokenizerFactory"
        mode="simple"/>
        </analyzer>
    </fieldtype>
    <!----检测模式分词: -->
    <fieldtype name="textSimple" class="solr.TextField">
        <analyzer>
            <tokenizer class="org.lionsoul.jcseg.analyzer.4x.JcsegTokenizerFactory"
        mode="detect"/>
        </analyzer>
    </fieldtype>
    
# elasticsearch集成Jcseg：

1. 下载最新版本的Jcseg源码。
2. 使用maven或者ant编译打包得到jcseg的系列jar包。
3. 拷贝jcseg-analyzer-{version}.jar,jcseg-core-{version}.jar,jcseg-elasticsearch-{version}.jar到{ES_HOME}/plugin/analysis-jcseg目录下(自己建立这个文件夹)。
4. 拷贝一份jcseg.properties到{ES_HOME}/config/jcseg目录下(自己建立文件
夹)。
5. 配置好jcseg.properties,尤其是配置lexicon.path正想正确的词库(可选,如果
身略步骤 4,则 jcseg按照先前上面的说自动搜寻jcseg.properties配置文件初始化选项)。
6. 参考下载的源码中的 jcseg-elasticsearch 项目下的 config/elasticsearch.yml 配置文件,将对应的配置加到{ES_HOME}/config/elasticsearch.yml中去。
7. 配置elasticsearch.yml或者mapping来使用jcseg分词插件(或者在query中指定)。
8. 可选的analyzer名字：
> jcseg: jcseg 的复杂模式切分算法 
> jcseg_comple: 对应 jcseg 的复杂模式切分算法 
> jcseg_simple: 对应 jcseg 的简易切分算法 
> jcseg_detect: 对应 jcseg 的检测模式切分算法

# 联系作者：

欢迎报告各种bug和建议到以下邮箱：
1. 陈鑫&lt;chenxin619315@gmail.com&gt;
2. 张仁芳&lt;dongyado@gmail.com&gt;