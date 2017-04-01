# Jcseg versions change histories

### TODO List: 
* 1. 同义词统一解决方案：同义词的相互引用，同义词追加和自动合并（同义词统一于一个词库类别管理，格式：根词/同义词1,同义词2）
* 2. lucene,solr,elasticsearch检索同义词解决方案与Jcseg同义词方案结合
* 3. 复杂模式，复杂中文的二次切分
* 4. 文本自动分类
* 5. 情感分析

### jcseg-2.1.1: (current version)

* 1. 优化JcsegTokenizer的实现：clearAttributes改为到reset中调用，去除end()的调用，方便TokenStream外引用做相关统计查询工作。
* 2. 修复Word#toString中json字符串的特殊字符转义bug，增加"和\的预处理。 reported by https://github.com/luohuan02
* 3. 修复《》之间五内容切出空字符串的bug。 reported by http://git.oschina.net/fige
* 4. NLP切分模式增加标准的datetime实体识别。例如：2017/03/07，2017-03-07。
* 5. NLP切分模式增加中文通用datetime实体识别。例如：2017年3月7日，明天下午4点半，下周二上午８点４５分等，明天凌晨2点一刻。
* 6. NLP切分模式增加混合dateime实体识别。例如：明天下午15:45，下周二10:30，2017-03-15下午三点半，2017/12/24下午15:45。
* 7. 优化了IWord词条对象的可能的并发访问问题，目前主要是出现在开启词库更新自动加载的情况下IWord.clone()调用时，更新线程和切分线程的竞争。

datetime实体识别测试demo（不同datetime部分使用空格分开，方便二次分词处理）：

```shell
jcseg~tokenzier:NLP>> 2017年3月2日
分词结果：
2017年 3月 2日/t/datetime.ymd
Done, total:9, tokens:1, in 0.00105sec
jcseg~tokenzier:NLP>> 2017年03月07日
分词结果：
2017年 03月 07日/t/datetime.ymd
Done, total:11, tokens:1, in 0.00000sec
jcseg~tokenzier:NLP>> 明天下午4点半
分词结果：
明天 下午 4点半/t/datetime.dahi
Done, total:7, tokens:1, in 0.00000sec
jcseg~tokenzier:NLP>> 下周二上午8点45分
分词结果：
下周二 上午 8点 45分/t/datetime.dahi
Done, total:10, tokens:1, in 0.00000sec
jcseg~tokenzier:NLP>> 2017年03月08日下午15点半去见一个投资人
分词结果：
2017年 03月 08日 下午 15点半/t/datetime.ymdahi 去/q/null 见/n/null 一个/q/null 投资人/n/null
Done, total:24, tokens:5, in 0.00000sec
jcseg~tokenzier:NLP>> 明天凌晨2点一刻产品升级开始
分词结果：
明天 凌晨 2点一刻/t/datetime.dahi 产品/n/null 升级/vn/null 开始/n/null
Done, total:14, tokens:4, in 0.00000sec
jcseg~tokenzier:NLP>> 明天下午15:45
分词结果：
明天 下午 15点 45分/t/datetime.dahi
Done, total:9, tokens:1, in 0.00000sec
jcseg~tokenzier:NLP>> 2017/03/15下午16:45:36开始生日party
分词结果：
2017/03/15 下午 16点 45分 36秒/t/datetime.ymdahis 开始/n/null 生日/n/null party/en/null
Done, total:29, tokens:4, in 0.00210sec
```

### jcseg-2.1.0: 

* 01. 部分词库类别合并到主类别（中英组合，英中组合，英文标点，英文词库），方便维护，也是为Jcseg的NLP计划做准备。
* 02. 优化Jcseg的英中组合词条的识别算法，之前的除类似“x射线”等英中混合词识别外，其他的类别的混合词维护过于麻烦，全部混合词库统一到lex-mixed.lex中管理或者新建词库。
* 03. 分隔符切分模式，对输入流直接按照单个分隔符（默认是空格）切分，特殊应用场景需求。
* 04. 词库增加词条实体标识和识别，方便应用对切分出来的词条做词条类别识别和应用，例如：时间，地点（比词性和实体识别更灵活，可以持有n种自定义实体类别）。
* 05. 优化了词库加载的检测（关于各类词条对于config.max_length的检测验证）。
* 06. 增加英文词条最大长度为64个字符的限制，防止输入很长的无空白英文字符串导致内存溢出
* 07. 新增了NLPSeg切分模式，用于NLP分析，继承自复杂模式，修改了数字，单位等词条的组合形式，增加电子邮件，大陆手机号码，网址，地名，人名，货币等实体的自动识别。
* 08. 优化了jcseg-server模块的api数据处理，简化了api数据返回格式。
* 09. 词库优化，将ip2region中的全部地域词库合并到了lex-place.lex中作为统一地名词库。
* 10. DictionaryFactory#createSingletonDictionary loadDic参数无效bug修复。
* 11. 增加对目前最新版本的lucene-6.3.0的支持。
* 12. 增加对目前最新版本的solr-6.3.0的支持。
* 13. 增加对目前最新版本的elasticsearch-5.1.1的支持。

升级指南：

* 1, 如果之前自定义过混合词库，则需要将混合词库的第一行的类别标识更改为：CJK_WORD，不然会不兼容而出现没法识别混合词的情况。
* 2, 如果之前自定义过词库文件格式，2.1.0开始词库文件硬编码使用“lex-”开头和“.lex”结尾，jcseg.properties中的配置项已经无效。

### jcseg-2.0.0:

* 1. 增加自定义词库开发文档
* 2. 完善关键字提取，关键短语提取，关键句子，自动摘要提取的自定义开发文档
* 3. 文档增加词库自动加载lex-autoload.todo权限提示
* 4. DictionaryFactory增加如下两个接口方便自定义词库开发

```java
createDefaultDictionary(JcsegTaskConfig config, boolean sync, boolean loadDic)
createSingletonDictionary(JcsegTaskConfig config, boolean loadDic)
/*
 * loadDic 用于控制工厂是否自动从config检测并且加载词库，兼容旧版本的默认是自动监测加载
 * 方便用户自定义加载自己的设置的词库
*/
```

* 5. 修复了并发情况下IWord#position可能的污染bug，这个bug会导致lucene的高亮错误
* 6. 优化了复杂英文组合的二次切分，确保返回词条后者的startOffset大于等于前者的

### jcseg-1.9.9: 

##### 1, 上传到了maven中心仓库，依赖地址如下：

* 1), jcseg (全部模块)

```xml
<dependency>
    <groupId>org.lionsoul</groupId>
    <artifactId>jcseg</artifactId>
    <version>1.9.9</version>
</dependency>
```

* 2), jcseg-core:

```xml
<dependency>
    <groupId>org.lionsoul</groupId>
    <artifactId>jcseg-core</artifactId>
    <version>1.9.9</version>
</dependency>

```

* 3), jcseg-analyzer (lucene或者solr):

```xml
<dependency>
    <groupId>org.lionsoul</groupId>
    <artifactId>jcseg-analyzer</artifactId>
    <version>1.9.9</version>
</dependency>
```

* 4), jcseg-elasticsearch

```xml
<dependency>
    <groupId>org.lionsoul</groupId>
    <artifactId>jcseg-elasticsearch</artifactId>
    <version>1.9.9</version>
</dependency>
```

* 5), jcseg-server (独立的应用服务器)

```xml
<dependency>
    <groupId>org.lionsoul</groupId>
    <artifactId>jcseg-server</artifactId>
    <version>1.9.9</version>
</dependency>
```

##### 2, JcsegTaskConfig更改构造方法如下：

```java
JcsegTaskConfig()                   //不做任何内部初始化
JcsegTaskConfig(boolean autoLoad)   //是否自动寻找配置文件
JcsegTaskConfig(String proFile)     //指定配置文件初始化
JcsegTaskConfig(InputStream is)     //指定输入流初始化
```

##### 3, JcsegTaskConfig或者jcseg.properties中的词库路劲支持设置为null
##### 4, ADictionary词库基类增加如下载入词库方法：

```java
load(File file)                 //从File中载入全部词条
load(String file)               //从指定文件路劲中载入全部词条
load(InputStream is)            //从输入流中载入全部词条
loadDirectory(String lexDir)    //载入一个词库目录下的全部词条
loadClassPath()                 //从classpath中载入全部词条
```

##### 5, jcseg-core-{version}.jar中自动打包了一份jcseg.properties和全部的词库，从此只需要jcseg-core-{version}.jar文件即可运行，无需任何依赖，
    同时也可以在jcseg-core-{version}.jar目录下存放一份jcseg.properties配置文件来自定义全部选项，例如：最大切分长度，自定义词库路径等。
##### 6, 词库优化，增加一些新词条


### jcseg-1.9.8: 

* 1. 增加检索切分模式（SEARCH_MODE），实现细粒度切分，专业为搜索。
* 2. 增加DictionaryFactory#createSingletonDictionary，用于创建单例词库。
* 3. 将analyzer,elasticsearch接口词库更改为单例创建，节省内存，同时避免了多实例的下词库自动加载无法全局更新的问题。
* 4. 提供对lucene,solr 6.0以上版本的支持，elasticsearch 2.3.1以上版本的支持。
* 5. 增加JcsegAnalyzer5X如下构造方法方便lucene应用的打包发布：

```java
JcsegAnalyzer5X(int mode, String proFile)
JcsegAnalyzer5X(int mode, JcsegTaskConfig config)
JcsegAnalyzer5X(int mode, JcsegTaskConfig config, ADictionary dic)
```
* 6. 代码格式标准化，例如：4空格代替tab，花括号的换行等。
* 7. 词库优化（去除些许无用词，完善部分词条词性定义）
* 8. 修复jcseg-server.properties#jcseg_global_setting名称错误, 更改为：jcseg_global_config。
* 9. 修复JcsegServer#http_config设置bug和TokenizerController#pos拼写错误。


### jcseg-1.9.7:

1. 词库优化：增加新词，词性优化
2. jcseg-server模块的restful api
3. 文章textRank关键词提取
4. textRank关键短语提取
5. 文章textRank关键句子／摘要提取
6. 终端测试程序增加关键字／关键短语／关键句子／摘要提取测试项


### jcseg-1.9.6:

1. mmseg过滤器的lazyInitRace bug修复
2. 同义词&拼音的高亮显示问题修复
3. detect模式增加位置返回和部分bug修复
4. 词库词性补全（完成绝大部分常用词条的词性补全）
5. 增加对最新版本的lucene(5.1.0), solr(5.1.0), elasticsearch(1.5.2)的支持
6. 词库autoload多目录支持bug修复


### jcseg-1.9.5:

1. 修复部分英中混合词的同义词无法追加的bug
2. 增加jcseg-elasticsearch模块 - elasticsearch切分插件
3. 修复切分词条的些许情况offset错误bug
4. 更改对solr-4.9的支持


### jcseg-1.9.4：

1. 改善中英混合词的识别,可以识别更多情况, 例如: 高3
2. 加入IHashQueue来替换原来的ILinkedList,改善word pool的性能,加快切分速度
3. 更改org.lionsoul.jcseg.util.STConverter加入了更全的简繁词条对照
4. 修复了中文数字转阿拉伯数字的一种会过滤的情况.(http://code.google.com/p/jcseg/issues/detail?id=22)
5. 修复词库路径空格bug:JAR_HOME = java.net.URLDecoder.decode("", "utf-8");
6. 修复中文数字转阿拉伯数字的一个bug.(例如：二零一四年会被转换成1914年)    
7. 检测模式切分(只切分返回词库中已有的词条,自动大小写，全半角转换)

### jcseg-1.9.3:

1. 更改为maven托管，同时也支持原始的ant编译。
2. 优化复杂英文组合的二次切分，减少了一些没必要的追加调用（不影响使用）。
3. 更换了复杂英文切分中二次切分词条和原词条的输出顺序，因为同义词追加的功能，二次切分的词条放在前面更合理。
4. 修复词库加载停止词长度限制判断的bug。
5. PushbackReader的内存溢出bug。


### jcseg-1.9.2:

1. 配置文件中词库多目录加载, 多个目录使用';'隔开.
2. 词库自动重载正则支持. --undone
2. 修复中文分数识别可能的一种错误组合的bug.    
3. 修复部分中文分数无法转换为阿拉伯分数的bug.
4. 词库合并工具bug修复.    
5. 词库加载错误友好提示.
6. 对复杂的数字和英文组合词进行再次切分, 例如: QQ2013, 切分成qq2013, qq, 2013.
7. 将自动保留的标点放置到了jcseg.properties配置文件中, 方便更改并且默认去除了对/,^等的保留.


### jcseg-1.9.1:

1. 优化IStringBuffer#resizeTo()方法, 使用System.arraycopy代替循环来拷贝元素.
2. 增加了util.dic.STConverter类来进行简体和繁体字符串的相互转换.
3. 增加了util.dic.DicConverter来管理jcseg词库简体和繁体相互转换.
4. 增加了util.dic.DicMerge类来合并jcseg的简体和繁体词库生成简繁体混合词库.
5. 将jcseg目前的简体词库经过转换得到了一份繁体词库(适合繁体切分).
6. 将简体词库和繁体词库进行了合并, 得到了一份简繁体混合词库.(简繁体通用, 但是会耗费更多内存).
7. 修复ASegment#nextBasicLatin()中"数字+空格+单位"组合中忽略空格直接组合数组和单位的bug.


### jcseg-1.9.0:

1. 修复"小数+单位"无法识别的情况.更改ASegment#isDigit()方法.
2. 词库加载长度限制(长度大于max_length的过滤掉).
3. 更改中英混合词的识别(目前可以识别中英任何一种组合).
```
    英中: 例如: b超,
    英中英: a美1,
    英中英中: a哆啦a梦,
    中英: 卡拉ok, 
    中英中: 哆啦a梦, 
    中英中英: 中文a美a
```
3. 更改了单位组合, 现在可以组合更多非中文单位, 例如: ℃,℉    
4. 对于未识别的字符, 给定一个开关选项来决定保留还是过滤.
5. 英文同义词的追加    (增加了lex-en.lex词库)


### jcseg-1.8.9:

1. 保留英文半全角标点和CJK标点符号切分(可通过停止词过滤掉,默认全部过滤掉了).
2. 词性标注(需要完成词库词性的标注).
3. IStringBuffer#deleteCharAt bug修复.
4. 修复英文字母和标点组合词(些许组合时无法识别)识别的一个bug.
5. 更改了词库加载时一个问题, 词库重载时, 防止重复加载同义词和词性.
6. 基本数据类型存储使用IntArrayList代替了ArrayList,减少了拆解箱.
7. 依据网友建议修改了开发帮助文档.

### jcseg-1.8.8:

1. JcsegTaskConfig自定义配置文件jcseg.properties管理(方便复杂项目管理).
2. 更改ADictionary类接口, 支持以词库文件为单位进行加载, 方便二次开发或者词库更新自动加载，词库分为同步词库和非同步词库.
3. 词库更新自动加载功能.
4. 整理jcseg.properties配置文件
5. 中英混合词识别(非英中混合词, 例如: b超), 例如: 卡拉ok.

### jcseg-1.8.7:

1.更改jcseg内部设计(更好的适用多线程).
> (1).加入JcsegTaskConfig描述Jcseg配置项(提供CloneFromProperties方法用于从jcseg.properties中克隆配置)
> (2).更改ASegment作用于JcsegTask.(由JcsegFactory产生指定算法和模式的分词工厂)
2.加上文章关键字提取功能.    -undone


### jcseg-1.8.6:

1. 修复些许词条(些许词库在切分过程中会进入中文数字识别程序中, 默认情况下jcseg没有追加中文数字的同义词)无法追加同义词的bug.
2. 加入所有类别词库停止词过滤功能, 先前版本只支持CJK词条切分停止词过滤.


### jcseg-1.8.5:

1. 加入了中文分数转阿拉伯分数的开关选项.
2. 加入solr分词接口.
3. 加入了README.txt
4. 加入了LISENSE.txt
5. 去除了某些词库类型词条加载长度限制, 例如stopwords的英文词长就不该有词长度限制.


### jcseg-1.8.3:

1. 修复了姓名识别方法,因lex-chars.lex中找不到对应的词而抛出NullPointerException的bug.
2. 加上中文数字转阿拉伯数字的开关选项,默认开启.
3. 彻底解决jcseg.properties文件查找:
```
    (1). jar home搜索, 如果没找到尝试(2)
    (2). classpath中搜索, 如果没找到尝试(3)
    (3). user home搜索. 如果没找到, 抛出异常, 并且会提示解决方法.
```


### jcseg-1.8.2:

1. 词库加载时,去除了长度大于Config.MAX_LENGTH的同义词.
2. 加上了英文的停止词过滤功能(并且加入了几百个英文停止词).
3. 加上了JcsegAnalyzer4X对lucene4.x版本的支持.
