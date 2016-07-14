
![Jcseg logo](http://dongyado.github.io/images/logo-x56.png)

# **Jcseg**是什么？
------

**Jcseg**是基于mmseg算法的一个轻量级中文分词器，同时集成了关键字提取，关键短语提取，关键句子提取和文章自动摘要等功能，并且提供了一个基于Jetty的web服务器，方便各大语言直接http调用，同时提供了最新版本的lucene, solr, elasticsearch的分词接口！**Jcseg**自带了一个 jcseg.properties文件用于快速配置而得到适合不同场合的分词应用，例如：最大匹配词长，是否开启中文人名识别，是否追加拼音，是否追加同义词等！

# **Jcseg**核心功能：
------

> * 中文分词：mmseg算法 + **Jcseg** 独创的优化算法，四种切分模式。
> * 关键字提取：基于textRank算法。
> * 关键短语提取：基于textRank算法。
> * 关键句子提取：基于textRank算法。
> * 文章自动摘要：基于BM25+textRank算法。
> * 自动词性标注：目前只是基于词库，效果不是很理想。
> * Restful api：嵌入jetty提供了一个绝对高性能的server模块，包含全部功能的http接口，标准化json输出格式，方便各种语言客户端直接调用。

# **Jcseg**中文分词：
------

**四种切分模式：**

> * (1).简易模式：FMM算法，适合速度要求场合。
> * (2).复杂模式-MMSEG四种过滤算法，具有较高的歧义去除，分词准确率达到了98.41%。
> * (3).检测模式：只返回词库中已有的词条，很适合某些应用场合。
> * (4).检索模式：细粒度切分，专为检索而生，除了中文处理外（不具备中文的人名，数字识别等智能功能）其他与复杂模式一致（英文，组合词等）。

1. 支持自定义词库。在lexicon文件夹下，可以随便添加/删除/更改词库和词库内容，并且对词库进行了分类。
2. 支持词库多目录加载. 配置lexicon.path中使用';'隔开多个词库目录.
3. 词库分为简体/繁体/简繁体混合词库: 可以专门适用于简体切分, 繁体切分, 简繁体混合切分, 并且可以利用下面提到的同义词实现,简繁体的相互检索,  **Jcseg**同时提供了词库两个简单的词库管理工具来进行简繁体的转换和词库的合并。
4. 中英文同义词追加/同义词匹配 + 中文词条拼音追加．词库整合了《现代汉语词典》和cc-cedict辞典中的词条，并且依据cc-cedict词典为词条标上了拼音，依据《中华同义词词典》为词条标上了同义词(尚未完成)。更改jcseg.properties配置文档可以在分词的时候加入拼音和同义词到分词结果中。
5. 中文数字和中文分数识别，例如："一百五十个人都来了，四十分之一的人。"中的"一百五十"和"四十分之一"。并且 **Jcseg**会自动将其转换为阿拉伯数字加入到分词结果中。如：150， 1/40。
6. 支持中英混合词和英中混合词的识别(维护词库可以识别任何一种组合)。例如：B超, x射线, 卡拉ok, 奇都ktv, 哆啦a梦。
7. 更好的英文支持，电子邮件，域名，小数，分数，百分数，字母和标点组合词（例如C++, c#）的识别。
8. 自定义切分保留标点. 例如: 保留&, 就可以识别k&r这种复杂词条。
9. 复杂英文切分结果的二次切分:  可以保留原组合，同时可以避免复杂切分带来的检索命中率下降的情况，例如QQ2013会被切分成: qq2013/ qq/ 2013,  chenxin619315@gmail.com会被切分成: chenxin619315@gmail.com/ chenxin/ 619315/ gmail/ com。
11. 支持阿拉伯数字/小数/中文数字基本单字单位的识别，例如2012年，1.75米，38.6℃，五折，并且 **Jcseg**会将其转换为“5折”加入分词结果中。
12. 智能圆角半角, 英文大小写转换。
13. 特殊字母识别：例如：Ⅰ，Ⅱ；特殊数字识别：例如：①，⑩。
14. 配对标点内容提取：例如：最好的Java书《java编程思想》，‘畅想杯黑客技术大赛’，被《,‘,“,『标点标记的内容。(1.6.8版开始支持)。
15. 智能中文人名/外文翻译人名识别。中文人名识别正确率达94%以上。（中文人名可以维护lex-lname.lex，lex-dname-1.lex，lex-dname-2.lex来提高准确率），(引入规则和词性后会达到98%以上的识别正确率)。
16. 自动中英文停止词过滤功能（需要在jcseg.properties中开启该选项，lex-stopwords.lex为停止词词库）。
17. 词库更新自动加载功能, 开启一个守护线程定时的检测词库的更新并且加载（**注意需要有对应词库目录下的的lex-autoload.todo文件的写入权限**）。
18. 自动词性标注（目前基于词库）。

# **Jcseg**快速体验：
------

#### 终端测试：

1. cd到 **Jcseg**根目录。
2. ant all(或者使用maven编译)
3. 运行：java -jar jcseg-core-{version}.jar
4. 你将看到如下的终端界面
5. 在光标处输入文本开始测试

```
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
jcseg~tokenizer>> 
```

#### 测试样板：

##### 分词文本

```
歧义和同义词:研究生命起源，混合词: 做B超检查身体，x射线本质是什么，今天去奇都ktv唱卡拉ok去，哆啦a梦是一个动漫中的主角，单位和全角: 2009年８月６日开始大学之旅，岳阳今天的气温为38.6℃, 也就是101.48℉, 中文数字/分数: 你分三十分之二, 小陈拿三十分之五,剩下的三十分之二十三全部是我的，那是一九九八年前的事了，四川麻辣烫很好吃，五四运动留下的五四精神。笔记本五折包邮亏本大甩卖。人名识别: 我是陈鑫，也是jcseg的作者，三国时期的诸葛亮是个天才，我们一起给刘翔加油，罗志高兴奋极了因为老吴送了他一台笔记本。外文名识别：冰岛时间7月1日，正在当地拍片的汤姆·克鲁斯通过发言人承认，他与第三任妻子凯蒂·赫尔墨斯（第一二任妻子分别为咪咪·罗杰斯、妮可·基德曼）的婚姻即将结束。配对标点: 本次『畅想杯』黑客技术大赛的得主为电信09-2BF的张三，奖励C++程序设计语言一书和【畅想网络】的『PHP教程』一套。特殊字母: 【Ⅰ】（Ⅱ），英文数字: bug report chenxin619315@gmail.com or visit http://code.google.com/p/jcseg, we all admire the hacker spirit!特殊数字: ① ⑩ ⑽ ㈩.
```

##### 分词结果：

```
歧义/n 和/o 同义词/n :/w 研究/vn 琢磨/vn 研讨/vn 钻研/vn 生命/n 起源/n ，/w 混合词 :/w 做/v b超/n 检查/vn 身体/n ，/w x射线/n x光线/n 本质/n 是/a 什么/n ，/w 今天/t 去/q 奇都ktv/nz 唱/n 卡拉ok/nz 去/q ，/w 哆啦a梦/nz 是/a 一个/q 动漫/n 中/q 的/u 主角/n ，/w 单位/n 和/o 全角/nz :/w 2009年/m 8月/m 6日/m 开始/n 大学/n 之旅 ，/w 岳阳/ns 今天/t 的/u 气温/n 为/u 38.6℃/m ,/w 也就是/v 101.48℉/m ,/w 中文/n 国语/n 数字/n //w 分数/n :/w 你/r 分/h 三十分之二/m ,/w 小陈/nr 拿/nh 三十分之五/m ,/w 剩下/v 的/u 三十分之二十三/m 全部/a 是/a 我的/nt ，/w 那是/c 一九九八年/m 1998年/m 前/v 的/u 事/i 了/i ，/w 四川/ns 麻辣烫/n 很/m 好吃/v ，/w 五四运动/nz 留下/v 的/u 五四/m 54/m 精神/n 。/w 笔记本/n 五折/m 5折/m 包邮 亏本/v 大甩卖 甩卖 。/w 人名/n 识别/v :/w 我/r 是/a 陈鑫/nr ，/w 也/e 是/a jcseg/en 的/u 作者/n ，/w 三国/mq 时期/n 的/u 诸葛亮/nr 是个 天才/n ，/w 我们/r 一起/d 给/v 刘翔/nr 加油/v ，/w 罗志高/nr 兴奋/v 极了/u 因为/c 老吴/nr 送了 他/r 一台 笔记本/n 。/w 外文/n 名/j 识别/v ：/w 冰岛/ns 时间/n 7月/m 1日/m ，/w 正在/u 当地/s 拍片/vi 的/u 汤姆·克鲁斯/nr 阿汤哥/nr 通过/v 发言人/n 承认/v ，/w 他/r 与/u 第三/m 任/q 妻子/n 凯蒂·赫尔墨斯/nr （/w 第一/a 二/j 任/q 妻子/n 分别为 咪咪·罗杰斯/nr 、/w 妮可·基德曼/nr ）/w 的/u 婚姻/n 即将/d 结束/v 。/w 配对/v 标点/n :/w 本次/r 『/w 畅想杯/nz 』/w 黑客/n 技术/n 大赛/vn 的/u 得主/n 为/u 电信/nt 09/en -/w bf/en 2bf/en 的/u 张三/nr ，/w 奖励/vn c++/en 程序设计/gi 语言/n 一书/ns 和/o 【/w 畅想网络/nz 】/w 的/u 『/w PHP教程/nz 』/w 一套/m 。/w 特殊/a 字母/n :/w 【/w Ⅰ/nz 】/w （/w Ⅱ/m ）/w ，/w 英文/n 英语/n 数字/n :/w bug/en report/en chenxin/en 619315/en gmail/en com/en chenxin619315@gmail.com/en or/en visit/en http/en :/w //w //w code/en google/en com/en code.google.com/en //w p/en //w jcseg/en ,/w we/en all/en admire/en appreciate/en like/en love/en enjoy/en the/en hacker/en spirit/en mind/en !/w 特殊/a 数字/n :/w ①/m ⑩/m ⑽/m ㈩/m ./w
```

# **Jcseg** Maven仓库：

Jcseg从1.9.8才开始上传到了maven仓库！

* 1. jcseg-core:

```xml
<dependency>
    <groupId>org.lionsoul</groupId>
    <artifactId>jcseg-core</artifactId>
    <version>1.9.9</version>
</dependency>

```

* 2. jcseg-analyzer (lucene或者solr):

```xml
<dependency>
    <groupId>org.lionsoul</groupId>
    <artifactId>jcseg-analyzer</artifactId>
    <version>1.9.9</version>
</dependency>
```

* 3. jcseg-elasticsearch

```xml
<dependency>
    <groupId>org.lionsoul</groupId>
    <artifactId>jcseg-elasticsearch</artifactId>
    <version>1.9.9</version>
</dependency>
```

* 4. jcseg-server (独立的应用服务器)

```xml
<dependency>
    <groupId>org.lionsoul</groupId>
    <artifactId>jcseg-server</artifactId>
    <version>1.9.9</version>
</dependency>
```


# **Jcseg** lucene分词接口：
------

1. 导入jcseg-core-{version}.jar和jcseg-analyzer-{version}.jar
2. demo代码：

```java
Analyzer analyzer = new JcsegAnalyzer5X(JcsegTaskConfig.COMPLEX_MODE);
//available constructor: since 1.9.8
//1, JcsegAnalyzer5X(int mode)
//2, JcsegAnalyzer5X(int mode, String proFile)
//3, JcsegAnalyzer5X(int mode, JcsegTaskConfig config)
//4, JcsegAnalyzer5X(int mode, JcsegTaskConfig config, ADictionary dic)

//lucene 4.x版本
//Analyzer analyzer = new JcsegAnalyzer4X(JcsegTaskConfig.COMPLEX_MODE);
//非必须(用于修改默认配置): 获取分词任务配置实例
JcsegAnalyzer5X jcseg = (JcsegAnalyzer5X) analyzer;
JcsegTaskConfig config = jcseg.getTaskConfig();
//追加同义词, 需要在 jcseg.properties中配置jcseg.loadsyn=1
config.setAppendCJKSyn(true);
//追加拼音, 需要在jcseg.properties中配置jcseg.loadpinyin=1
config.setAppendCJKPinyin();
//更多配置, 请查看 org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig
```

# **Jcseg** solr分词接口：
------

1. 将jcseg-core-{version}.jar和jcseg-analyzer-{version}.jar 复制到solr 的类库目录中。
2. 在solr的scheme.xml加入如下两种配置之一：

```xml
<!-- 复杂模式分词: -->
<fieldtype name="textComplex" class="solr.TextField">
    <analyzer>
        <tokenizer class="org.lionsoul.jcseg.analyzer.v5x.JcsegTokenizerFactory" mode="complex"/>
    </analyzer>
</fieldtype>
<!-- 简易模式分词: -->
<fieldtype name="textSimple" class="solr.TextField">
    <analyzer>
        <tokenizer class="org.lionsoul.jcseg.analyzer.v5x.JcsegTokenizerFactory" mode="simple"/>
    </analyzer>
</fieldtype>
<!-- 检测模式分词: -->
<fieldtype name="textDetect" class="solr.TextField">
    <analyzer>
        <tokenizer class="org.lionsoul.jcseg.analyzer.v5x.JcsegTokenizerFactory" mode="detect"/>
    </analyzer>
</fieldtype>
<!-- 最多模式分词: -->
<fieldtype name="textSearch" class="solr.TextField">
    <analyzer>
        <tokenizer class="org.lionsoul.jcseg.analyzer.v5x.JcsegTokenizerFactory" mode="search"/>
    </analyzer>
</fieldtype>
```

注：如果使用的是solr-4.x版本，请下载v1.9.7-release tag下的源码编译得到对应的jar，然后将上述xml中的v5x改成v4x即可。

# **Jcseg** elasticsearch接口：
------

##### elasticsearch.version < 2.x (Not sure)

1. 下载最新版本的 **Jcseg**源码。
2. 使用maven或者ant编译打包得到 **Jcseg**的系列jar包（建议使用maven，ant需要自己下载对应的依赖包）。
3. 拷贝jcseg-analyzer-{version}.jar,jcseg-core-{version}.jar,jcseg-elasticsearch-{version}.jar到{ES_HOME}/plugins/analysis-jcseg目录下（自己建立该文件夹，如果不存在）。
4. 拷贝一份jcseg.properties到{ES_HOME}/config/jcseg目录下（自己建立该文件夹，如果不存在）。
5. 配置好jcseg.properties,尤其是配置lexicon.path指向正确的词库（或者将jcseg目录下的lexicon文件夹拷贝到{ES_HOME}/plugins/jcseg目录下）。
6. 参考下载的源码中的 jcseg-elasticsearch 项目下的 config/elasticsearch.yml 配置文件,将对应的配置加到{ES_HOME}/config/elasticsearch.yml中去。
7. 配置elasticsearch.yml或者mapping来使用 **Jcseg**分词插件(或者在query中指定)。

##### elasticsearch.version >= 2.x

1. 下载最新版本的 **Jcseg**源码。
2. 使用maven或者ant编译打包得到 **Jcseg**的系列jar包（建议使用maven，ant需要自己下载对应的依赖包）。
3. 拷贝jcseg-analyzer-{version}.jar,jcseg-core-{version}.jar,jcseg-elasticsearch-{version}.jar到{ES_HOME}/plugins/jcseg目录下（自己建立该文件夹，如果不存在）。
4. 拷贝一份jcseg.properties到{ES_HOME}/plugins/jcseg目录下（自己建立该文件夹，如果不存在）。
5. 拷贝一份jcseg-elasticsearch/plugin/plugin-descriptor.properties到{ES_HOME}/plugins/jcseg目录下（自己建立该文件夹，如果不存在）。
6. 配置好jcseg.properties,尤其是配置lexicon.path指向正确的词库（或者将jcseg目录下的lexicon文件夹拷贝到{ES_HOME}/plugins/jcseg目录下）。
7. 参考下载的源码中的 jcseg-elasticsearch 项目下的 config/elasticsearch.yml 配置文件,将对应的配置加到{ES_HOME}/config/elasticsearch.yml中去，以下配置可以不用了：
8. 配置elasticsearch.yml或者mapping来使用 **Jcseg**分词插件(或者在query中指定)。


可选的analyzer名字：

```
jcseg_complex: 对应Jcseg的复杂模式切分算法 
jcseg_simple : 对应Jcseg的简易切分算法 
jcseg_detect : 对应Jcseg的检测模式切分算法
jcseg_search : 对应Jcseg的检索模式切分算法

```

也可以直接使用集成了jcseg的elasticsearch运行包：[elasticsearch-jcseg](http://git.oschina.net/lionsoul/elasticsearch-jcseg)，开封就可以使用。


# **Jcseg**分词服务器:
------

jcseg-server模块嵌入了jetty，实现了一个绝对高性能的服务器，给jcseg的全部功能都加上了restful接口，并且标准化了api结果的json输出格式，各大语言直接使用http客户端调用即可。

### 编译jcseg: 
1. maven编译jcseg，得到jcseg-server-{version}.jar, maven已经将依赖的jar包一起编译进去了，如果是ant编译运行时请将依赖包载入。
2. 启动jcseg server：

```bash
# 在最后传入jcseg-server.properties配置文件的路径
java -jar jcseg-server-{version}.jar ./jcseg-server.properties
```

### jcseg-server.properties:

懒得翻译了，默默的多念几遍就会了！

```
# jcseg server configuration file with standard json syntax
{
    # jcseg server configuration
    "server_config": {
        # server port
        "port": 1990,
        
        # default conmunication charset
        "charset": "utf-8",
        
        # http idle timeout in ms
        "http_connection_idle_timeout": 60000,
        
        # jetty maximum thread pool size
        "max_thread_pool_size": 200,
        
        # thread idle timeout in ms
        "thread_idle_timeout": 30000,
        
        # http output buffer size
        "http_output_buffer_size": 32768,
        
        # request header size
        "http_request_header_size": 8192,
        
        # response header size
        "http_response_header_size": 8192
    },
    
    
    # global setting for jcseg, yet another copy of the old 
    # configuration file jcseg.properties
    "jcseg_global_config": {
        # maximum match length. (5-7)
        "jcseg_maxlen": 7,
        
        # recognized the chinese name.
        # (true to open and false to close it)
        "jcseg_icnname": true,
        
        # maximum chinese word number of english chinese mixed word. 
        "jcseg_mixcnlen": 3,
        
        # maximum length for pair punctuation text.
        # set it to 0 to close this function
        "jcseg_pptmaxlen": 7,
        
        # maximum length for chinese last name andron.
        "jcseg_cnmaxlnadron": 1,
        
        # Wether to clear the stopwords.
        # (set true to clear stopwords and false to close it)
        "jcseg_clearstopword": false,
        
        # Wether to convert the chinese numeric to arabic number. 
        # (set to true open it and false to close it) like '\u4E09\u4E07' to 30000.
        "jcseg_cnnumtoarabic": true,
        
        # Wether to convert the chinese fraction to arabic fraction.
        # @Note: for lucene,solr,elasticsearch eg.. close it.
        "jcseg_cnfratoarabic": false,
        
        # Wether to keep the unrecognized word. 
        # (set true to keep unrecognized word and false to clear it)
        "jcseg_keepunregword": true,
        
        # Wether to start the secondary segmentation for the complex english words.
        "jcseg_ensencondseg": true,
        
        # min length of the secondary simple token. 
        # (better larger than 1)
        "jcseg_stokenminlen": 2,
        
        #thrshold for chinese name recognize.
        # better not change it before you know what you are doing.
        "jcseg_nsthreshold": 1000000,
        
        #The punctuations that will be keep in an token.
        # (Not the end of the token).
        "jcseg_keeppunctuations": "@#%.&+"
    },
    
    # dictionary instance setting.
    # add yours here with standard json syntax
    "jcseg_dict": {
        "master": {
            "path": [
                "{jar.dir}/lexicon"
                # absolute path here
                #"/java/JavaSE/jcseg/lexicon"
            ],
            
            # wether to load the part of speech of the words
            "loadpos": true,

            # Wether to load the pinyin of the words.
            "loadpinyin": true,

            # Wether to load the synoyms words of the words.
            "loadsyn": true,
                    
            # Wether to load the modified lexicon file auto.
            "autoload": true,
        
            # Poll time for auto load. (in seconds)
            "polltime": 300
        }
        
        # add more of yours here
        # ,"name" : {
        #   "path": [
        #       "absolute jcseg standard lexicon path 1",
        #       "absolute jcseg standard lexicon path 2"
        #       ...
        #   ],
        #   "autoload": 0,
        #   "polltime": 300
        # }
    },
    
    # JcsegTaskConfig instance setting.
    # @Note: 
    # All the config instance here is extends from the global_setting above.
    # do nothing will extends all the setting from global_setting
    "jcseg_config": {
        "master": {
            # extends and Override the global setting
            "jcseg_pptmaxlen": 0,
            "jcseg_cnfratoarabic": true,
            "jcseg_keepunregword": false
        }
        
        # this one is for keywords,keyphrase,sentence,summary extract
        # @Note: do not delete this instance if u want jcseg to
        # offset u extractor service
        ,"extractor": {
            "jcseg_pptmaxlen": 0,
            "jcseg_clearstopword": true,
            "jcseg_cnnumtoarabic": false,
            "jcseg_cnfratoarabic": false,
            "jcseg_keepunregword": false,
            "jcseg_ensencondseg": false
        }
        
        # add more of yours here
        # ,"name": {
        #   ...
        # }
    },
    
    # jcseg tokenizer instance setting.
    # Your could let the instance service for you by access:
    # http://jcseg_server_host:port/tokenizer/instance_name
    # instance_name is the name of instance you define here.
    "jcseg_tokenizer": {
        "master": {
            # jcseg tokenizer algorithm, could be:
            # 1: SIMPLE_MODE
            # 2: COMPLEX_MODE
            # 3: DETECT_MODE
            # 4: SEARCH_MODE
            # see org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig for more info
            "algorithm": 2,
            
            # dictionary instance name
            # choose one of your defines above in the dict scope
            "dict": "master",
            
            # JcsegTaskConfig instance name
            # choose one of your defines above in the config scope
            "config": "master"
        }
        
        # this tokenizer instance is for extractor service
        # do not delete it if you want jcseg to offset you extractor service
        ,"extractor": {
            "algorithm": 2,
            "dict": "master",
            "config": "extractor"
        }
        
        # add more of your here
        # ,"name": {
        #   ...
        # }
    }
}
```

### restful api:

##### 1. 关键字提取：
> api地址：http://jcseg_server_host:port/extractor/keywords?text=&number=&autoFilter=true|false

> api参数：
    <pre>
    text: post或者get过来的文档文本
    number: 要提取的关键词个数
    autoFilter: 是否自动过滤掉低分数关键字
    </pre>
> api返回：

```
{
    //api执行状态, true：成功, false：失败。
    "status": true,
    //api返回数据
    "data": {
        //关键字数组
        "keywords": [],
        //操作耗时
        "took": 0.001
    },
    //api错误代号，0正常，1参数错误
    "errcode": 0
}
```
    
更多配置请参考：org.lionsoul.jcseg.server.controller.KeywordsController

##### 2. 关键短语提取：
> api地址：http://jcseg_server_host:port/extractor/keyphrase?text=&number=

> api参数：
    <pre>
    text: post或者get过来的文档文本
    number: 要提取的关键短语个数
    </pre>
> api返回：

```
{
    "status": true,
    "data": {
        "took": 0.0277,
        //关键短语数组
        "keyphrase": []
    },
    "errcode": 0
}
```
    
更多配置请参考：org.lionsoul.jcseg.server.controller.KeyphraseController

##### 3. 关键句子提取：
> api地址：http://jcseg_server_host:port/extractor/sentence?text=&number=

> api参数：
    <pre>
    text: post或者get过来的文档文本
    number: 要提取的关键句子个数
    </pre>
> api返回：

```
{
    "status": true,
    "data": {
        "took": 0.0277,
        //关键句子数组
        "sentence": []
    },
    "errcode": 0
}
```
    
更多配置请参考：org.lionsoul.jcseg.server.controller.SentenceController

##### 4. 文章摘要提取：
> api地址：http://jcseg_server_host:port/extractor/summary?text=&length=

> api参数：
    <pre>
    text: post或者get过来的文档文本
    length: 要提取的摘要的长度
    </pre>
> api返回：

```
{
    "status": true,
    "data": {
        "took": 0.0277,
        //文章摘要
        "summary": ""
    },
    "errcode": 0
}
```
    
更多配置请参考：org.lionsoul.jcseg.server.controller.SummaryController

##### 5. 文章自动分词：
> api地址：http://jcseg_server_host:port/tokenizer/tokenizer_instance?text=&ret_pinyin=&ret_pos=...

> api参数：
    <pre>
    tokenizer_instance: 表示在jcseg-server.properties中定义的分词实例名称
    text: post或者get过来的文章文本
    ret_pinyin: 是否在分词结果中返回词条拼音
    ret_pos: 是否在分词结果中返回词条词性
    </pre>
> api返回：

```
{
    "status": true,
    "data": {
        "took": 0.00885,
        //词条对象数组
        "list": [
            {
                //词条内容
                "word": "jcseg",
                //词条字符个数
                "length": 5,
                //词条在原文中的起始位置
                "position": 0
                //词条pinyin，如果指定了ret_pinyin
                "pinyin": "",
                //词条词性：如果指定了ret_pos
                "pos": ""
            }
        ]
    },
    "errcode": 0
}
```

更多配置请参考：org.lionsoul.jcseg.server.controller.TokenizerController

# *Jcseg*二次开发:
------

### 1. Jcseg中文分词Api: 

##### (1). 创建JcsegTaskConfig配置对象：

jcseg.properties查找步骤：

* 1，寻找jcseg-core-{version}.jar目录下的jcseg.properties
* 2，如果没找到继续寻找classpath下的jcseg.properties（默认已经打包了）
* 3，如果没找到继续寻找user home下的jcseg.properties（除非把classpath下的jcseg.properties删除了，要不然不会到这）

所以，默认情况下可以在jcseg-core-{version}.jar同目录下来放一份jcseg.properties来自定义配置。


JcsegTaskConfig构造方法如下：

```java
JcsegTaskConfig();                          //不做任何配置文件查找来初始化
JcsegTaskConfig(boolean autoLoad);          //autoLoad=true会自动查找配置来初始化
JcsegTaskConfig(java.lang.String proFile);  //从指定的配置文件中初始化配置对象
JcsegTaskConfig(InputStream is);            //从指定的输入流中初始化配置对象
```

demo代码：
  
```java
//创建JcsegTaskConfig使用默认配置，不做任何配置文件查找
JcsegTaskConfig config = new JcsegTaskConfig();

//该方法会自动按照上述“jcseg.properties查找步骤”来寻找jcseg.properties并且初始化：
JcsegTaskConfig config = new JcsegTaskConfig(true);

//依据给定的jcseg.properties文件创建并且初始化JcsegTaskConfig
JcsegTaskConfig config = new JcsegTaskConfig("absolute or relative jcseg.properties path");

//调用JcsegTaskConfig#load(String proFile)方法来从指定配置文件中初始化配置选项
config.load("absolute or relative jcseg.properties path");
```

##### (2). 创建ADictionary词库对象：

ADictionary构造方法如下：

```java
ADictionary(JcsegTaskConfig config, java.lang.Boolean sync)
//config：上述的JcsegTaskConfig实例
//sync: 是否创建线程安全词库，如果你需要在运行时操作词库对象则指定true，
//      如果jcseg.properties中autoload=1则会自动创建同步词库
```

demo代码：

```java
//Jcseg提供org.lionsoul.jcseg.tokenzier.core.DictionaryFactory来方便词库的创建与往后的兼容
//通常可以通过
//  DictionaryFactory#createDefaultDictionary(JcsegTaskConfig)
//  DictionaryFactory.createSingletonDictionary(JcsegTaskConfig)
//两方法来创建词库对象并且加载词库文件，建议使用createSingletonDictionary来创建单例词库

//config为上面创建的JcsegTaskConfig对象.
//如果给定的JcsegTaskConfig里面的词库路径信息正确
//ADictionary会依据config里面的词库信息加载全部有效的词库;
//并且该方法会依据config.isAutoload()来决定词库的同步性还是非同步性,
//config.isAutoload()为true就创建同步词库, 反之就创建非同步词库,
//config.isAutoload()对应jcseg.properties中的lexicon.autoload;
//如果config.getLexiconPath() = null，DictionaryFactory会自动加载classpath下的词库
//如果不想让其自动加载lexicon下的词库
//可以调用：DictionaryFactory.createSingletonDictionary(config, false)创建ADictionary即可；
ADictionary dic = DictionaryFactory.createSingletonDictionary(config);


//创建一个非同步的按照config.lexPath配置加载词库的ADictioanry.
ADictionary dic = DictionaryFactory.createDefaultDictionary(config, false);
//创建一个同步的按照config.lexPath加载词库的ADictioanry.
ADictionary dic = DictionaryFactory.createDefaultDictionary(config, true);
//依据 config.isAutoload()来决定同步性，默认按照config.lexPath来加载词库的ADictionary
ADictionary dic = DictionaryFactory.createDefaultDictionary(config, config.isAutoload());


//指定ADictionary加载给定目录下的所有词库文件的词条.
//config.getLexiconPath为词库文件存放有效目录数组.
for ( String path : config.getLexiconPath() ) {
    dic.loadDirectory(path);
}

//指定ADictionary加载给定词库文件的词条.
dic.load("/java/lex-main.lex");
dic.load(new File("/java/lex-main.lex"));

//指定ADictionary加载给定输入流的词条
dic.load(new FileInputStream("/java/lex-main.lex"));

//阅读下面的“如果自定义使用词库”来获取更多信息
```

##### (3). 创建ISegment或者ASegment分词实例：

ASegment构造方法：

```java
Asegment(JcsegTaskConfig config, Adictionary dic)
Asegment(Reader input, JcsegTaskConfig config, Adictionary dic)
//config: 为上述的JcsegTaskConfig配置对象
//dic: 为上述的ADictionary词库对象
//input: 分词文本源的输入对象
```

demo代码：
    
```java
//依据给定的ADictionary和JcsegTaskConfig来创建ISegment
//通常使用SegmentFactory#createJcseg来创建ISegment对象
//将config和dic组成一个Object数组给SegmentFactory.createJcseg方法
//JcsegTaskConfig.COMPLEX_MODE表示创建ComplexSeg复杂ISegment分词对象
//JcsegTaskConfig.SIMPLE_MODE表示创建SimpleSeg简易Isegmengt分词对象.
//JcsegTaskConfig.DETECT_MODE表示创建DetectSeg Isegmengt分词对象.
//JcsegTaskConfig.SEARCH_MODE表示创建SearchSeg Isegmengt分词对象.
ASegment seg = SegmentFactory.createJcseg(
    JcsegTaskConfig.COMPLEX_MODE, 
    new Object[]{config, dic}
);
    
//设置要分词的内容
String str = "研究生命起源。";
seg.reset(new StringReader(str));
    
//获取分词结果
IWord word = null;
while ( (word = seg.next()) != null ) {
    System.out.println(word.getValue());
}
```

##### (4). 一个完整的例子：

```java
//创建JcsegTaskConfig分词配置实例，自动查找加载jcseg.properties配置项来初始化
JcsegTaskConfig config = new JcsegTaskConfig(true);

//创建默认单例词库实现，并且按照config配置加载词库
ADictionary dic = DictionaryFactory.createSingletonDictionary(config);

//依据给定的ADictionary和JcsegTaskConfig来创建ISegment
//为了Api往后兼容，建议使用SegmentFactory来创建ISegment对象
ASegment seg = SegmentFactory.createJcseg(
    JcsegTaskConfig.COMPLEX_MODE, 
    new Object[]{new StringReader(str), config, dic}
);


//备注：以下代码可以反复调用，seg为非线程安全

//设置要被分词的文本
String str = "研究生命起源。";
seg.reset(new StringReader(str));

//获取分词结果
IWord word = null;
while ( (word = seg.next()) != null ) {
    System.out.println(word.getValue());
}
```

##### (5). 如何自定义使用词库：

从1.9.9版本开始，Jcseg已经默认将jcseg.properties和lexicon全部词库打包进了jcseg-core-{version}.jar中，如果是通过JcsegTaskConfig(true)构造的JcsegTaskConfig或者调用了JcsegTaskConfig#autoLoad()方法，在找不到自定义配置文件情况下Jcseg会自动的加载classpath中的配置文件，如果config.getLexiconPath() = null DictionaryFactory默认会自动加载classpath下的词库。

* 1)，通过JcsegTaskConfig设置词库路径：

```java
//1, 默认构造JcsegTaskConfig，不做任何配置文件寻找来初始化
JcsegTaskConfig config = new JcsegTaskConfig();

//2, 设置自定义词库路径集合
config.setLexiconPath(new String[]{
    "relative or absolute lexicon path1",
    "relative or absolute lexicon path2"
    //add more here
});

//3, 通过config构造词库并且DictionaryFactory会按照上述设置的词库路径自动加载全部词库
ADictionary dic = DictionaryFactory.createSingletonDictionary(config);
```

* 2)，通过ADictionary手动加载词库：

```java
//1, 构造默认的JcsegTaskConfig，不做任何配置文件寻找来初始化
JcsegTaskConfig config = new JcsegTaskConfig();

//2, 构造ADictionary词库对象
//注意第二个参数为false，阻止DictionaryFactory自动检测config.getLexiconPath()来加载词库
ADictionary dic = DictionaryFactory.createSingletonDictionary(config, false);

//3, 手动加载词库
dic.load(new File("absolute or relative lexicon file path"));              //加载指定词库文件下全部词条
dic.load("absolute or relative lexicon file path");                        //加载指定词库文件下全部词条
dic.load(new FileInputStream("absolute or relative lexicon file path"));   //加载指定InputStream输入流下的全部词条
dic.loadDirectory("absolute or relative lexicon directory");       //加载指定目录下的全部词库文件的全部词条
dic.loadClassPath();        //加载classpath路径下的全部词库文件的全部词条（默认路径/lexicon）
```

### 2. Jcseg关键字提取Api：

* 1)，TextRankKeywordsExtractor构造方法：

```java
TextRankKeywordsExtractor(ISegment seg);
//seg: Jcseg ISegment分词对象
```

* 2)，demo代码：

```java
//1, 创建Jcseg ISegment分词对象
JcsegTaskConfig config = new JcsegTaskConfig(true);
config.setClearStopwords(true);     //设置过滤停止词
config.setAppendCJKSyn(false);      //设置关闭同义词追加
config.setKeepUnregWords(false);    //设置去除不识别的词条
ADictionary dic = DictionaryFactory.createSingletonDictionary(config);
Segment seg = SegmentFactory.createJcseg(
    JcsegTaskConfig.COMPLEX_MODE, 
    new Object[]{config, dic}
);

//2, 构建TextRankKeywordsExtractor关键字提取器
TextRankKeywordsExtractor extractor = new TextRankKeywordsExtractor(seg);
extractor.setMaxIterateNum(100);        //设置pagerank算法最大迭代次数，非必须，使用默认即可
extractor.setWindowSize(5);             //设置textRank计算窗口大小，非必须，使用默认即可
extractor.setKeywordsNum(10);           //设置最大返回的关键词个数，默认为10

//3, 从一个输入reader输入流中获取关键字
String str = "现有的分词算法可分为三大类：基于字符串匹配的分词方法、基于理解的分词方法和基于统计的分词方法。按照是否与词性标注过程相结合，又可以分为单纯分词方法和分词与标注相结合的一体化方法。";
List<String> keywords = extractor.getKeywords(new StringReader(str));

//4, output:
//"分词","方法","分为","标注","相结合","字符串","匹配","过程","大类","单纯"
```

* 3)，测试源码参考：org.lionsoul.jcseg.test.KeywordsExtractorTest源码

### 3. Jcseg自动摘要/关键句子提取Api：

* 1)，TextRankSummaryExtractor构造方法：

```java
TextRankSummaryExtractor(ISegment seg, SentenceSeg sentenceSeg);
//seg: Jcseg ISegment分词对象
//sentenceSeg: Jcseg SentenceSeg句子切分对象
```

* 2)，demo代码：

```java
//1, 创建Jcseg ISegment分词对象
JcsegTaskConfig config = new JcsegTaskConfig(true);
config.setClearStopwords(true);     //设置过滤停止词
config.setAppendCJKSyn(false);      //设置关闭同义词追加
config.setKeepUnregWords(false);    //设置去除不识别的词条
ADictionary dic = DictionaryFactory.createSingletonDictionary(config);
Segment seg = SegmentFactory.createJcseg(
    JcsegTaskConfig.COMPLEX_MODE, 
    new Object[]{config, dic}
);

//2, 构造TextRankSummaryExtractor自动摘要提取对象
SummaryExtractor extractor = new TextRankSummaryExtractor(seg, new SentenceSeg());


//3, 从一个Reader输入流中获取length长度的摘要
String str = "Jcseg是基于mmseg算法的一个轻量级开源中文分词器，同时集成了关键字提取，关键短语提取，关键句子提取和文章自动摘要等功能，并且提供了最新版本的lucene,%20solr,%20elasticsearch的分词接口。Jcseg自带了一个%20jcseg.properties文件用于快速配置而得到适合不同场合的分词应用。例如：最大匹配词长，是否开启中文人名识别，是否追加拼音，是否追加同义词等！";
String summary = extractor.getSummary(new StringReader(str), 64);

//4, output:
//Jcseg是基于mmseg算法的一个轻量级开源中文分词器，同时集成了关键字提取，关键短语提取，关键句子提取和文章自动摘要等功能，并且提供了最新版本的lucene, solr, elasticsearch的分词接口。


//-----------------------------------------------------------------
//5, 从一个Reader输入流中提取n个关键句子
String str = "you source string here";
extractor.setSentenceNum(6);        //设置返回的关键句子个数
List<String> keySentences = extractor.getKeySentence(new StringReader(str));
```

* 3)，测试源码参考：org.lionsoul.jcseg.test.SummaryExtractorTest源码

### 4. Jcseg关键短语提取Api：

* 1)，TextRankKeyphraseExtractor构造方法：

```java
TextRankKeyphraseExtractor(ISegment seg);
//seg: Jcseg ISegment分词对象
```

* 2)，demo代码：

```java
//1, 创建Jcseg ISegment分词对象
JcsegTaskConfig config = new JcsegTaskConfig(true);
config.setClearStopwords(false);    //设置不过滤停止词
config.setAppendCJKSyn(false);      //设置关闭同义词追加
config.setKeepUnregWords(false);    //设置去除不识别的词条
config.setEnSecondSeg(false);       //关闭英文自动二次切分
ADictionary dic = DictionaryFactory.createSingletonDictionary(config);
Segment seg = SegmentFactory.createJcseg(
    JcsegTaskConfig.COMPLEX_MODE, 
    new Object[]{config, dic}
);

//2, 构建TextRankKeyphraseExtractor关键短语提取器
TextRankKeyphraseExtractor extractor = new TextRankKeyphraseExtractor(seg);
extractor.setMaxIterateNum(100);        //设置pagerank算法最大迭代词库，非必须，使用默认即可
extractor.setWindowSize(5);             //设置textRank窗口大小，非必须，使用默认即可
extractor.setKeywordsNum(15);           //设置最大返回的关键词个数，默认为10
extractor.setMaxWordsNum(4);            //设置最大短语词长，默认为5

//3, 从一个输入reader输入流中获取短语
String str = "支持向量机广泛应用于文本挖掘，例如，基于支持向量机的文本自动分类技术研究一文中很详细的介绍支持向量机的算法细节，文本自动分类是文本挖掘技术中的一种！";
List<String> keyphrases = extractor.getKeyphrase(new StringReader(str));

//4, output:
//支持向量机, 自动分类
```

* 3)，测试源码参考：org.lionsoul.jcseg.test.KeyphraseExtractorTest源码

> This is the end line and thanks for watching !!!
