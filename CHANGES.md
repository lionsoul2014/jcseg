# Jcseg versions change histories

### jcseg-2.0.1: (next version)

* 1. 复杂模式，复杂中文的二次切分
* 2. 文本自动分类

### jcseg-2.0.0: (snapshot version)

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

### jcseg-1.9.9: (current version)

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
