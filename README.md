Jcseg完整版本(源码, 词库, 帮助文档, 词库管理工具, jar文件)下载:  https://code.google.com/p/jcseg <br />

<h3>一. 关于jcseg: </h3>

jcseg是使用Java开发的一个开源中文分词器，使用流行的mmseg算法实现，并且提供了最高版本的lucene-4.x和最高版本solr-4.x的分词接口．


1。目前最高版本：jcseg-1.9.2。兼容最高版本lucene-4.x和最高版本solr-4.x

2。mmseg四种过滤算法，分词准确率达到了98.41%。

3。支持自定义词库。在lexicon文件夹下，可以随便添加/删除/更改词库和词库内容，并且对词库进行了分类。参考下面了解如何给jcseg添加词库/新词。

4。<font color="red">(!New)</font> 支持词库多目录加载. 配置lexicon.path中使用';'隔开多个词库目录.

5。<font color="red">(!New)</font>词库分为简体/繁体/简繁体混合词库: 可以专门适用于简体切分, 繁体切分, 简繁体混合切分, 并且可以利用下面提到的同义词实现,简繁体的相互检索,  jcseg同时提供了词库两个简单的词库管理工具来进行简繁体的转换和词库的合并.

6。中英文同义词追加/同义词匹配 + 中文词条拼音追加．词库整合了《现代汉语词典》和cc-cedict辞典中的词条，并且依据cc-cedict词典为词条标上了拼音，依据《中华同义词词典》为词条标上了同义词(尚未完成)。更改jcseg.properties配置文档可以在分词的时候加入拼音和同义词到分词结果中。

7。中文数字和中文分数识别，例如："一百五十个人都来了，四十分之一的人。"中的"一百五十"和"四十分之一"。并且jcseg会自动将其转换为阿拉伯数字加入到分词结果中。如：150， 1/40。

8。支持中英混合词和英中混合词的识别(维护词库可以识别任何一种组合)。例如：B超, x射线, 卡拉ok, 奇都ktv, 哆啦a梦。

9。更好的英文支持，电子邮件，域名，小数，分数，百分数，字母和标点组合词（例如C++, c#）的识别。

10。<font color="red">(!New)</font>自定义切分保留标点. 例如: 保留&, 就可以识别k&r这种复杂词条。

11。<font color="red">(!New)</font>复杂英文切分结果的二次切分: 例如QQ2013会被切分成: qq2013/ qq/ 2013,  chenxin619315@gmail.com会被切分成: chenxin619315@gmail.com/ chenxin/ 619315/ gmail/ com。

12。支持阿拉伯数字/小数/中文数字基本单字单位的识别，例如2012年，1.75米，38.6℃，五折，并且jcseg会将其转换为“5折”加入分词结果中。

13。智能圆角半角, 英文大小写转换。

14。特殊字母识别：例如：Ⅰ，Ⅱ

15。特殊数字识别：例如：①，⑩

16。配对标点内容提取：例如：最好的Java书《java编程思想》，‘畅想杯黑客技术大赛’，被《,‘,“,『标点标记的内容。(1.6.8版开始支持)。

17。智能中文人名识别。中文人名识别正确率达94%以上。（可以维护lex-lname.lex，lex-dname-1.lex，lex-dname-2.lex来提高准确率），(引入规则和词性后会达到98%以上的识别正确率)。

18。自动中英文停止词过滤功能（需要在jcseg.properties中开启该选项，lex-stopwords.lex为停止词词库）。

19。词库更新自动加载功能, 开启一个守护线程随时检测词库的更新并且加载。

20。<font color="red">(!New)</font>自动词性标注。

21。jcseg.properties --- 方便自主对分词进行配置, 打造适合你的应用的分词.

jcseg佩带了一个jcseg.properties文件，只要有使用文本编辑器你就可以自主的编辑里面的选项，配置适合不同场合的分词应用。例如：最大匹配词数，是否开启中文人名识别，是否记载词条拼音，是否载入词条同义词等等。 如何配置jcseg


<h3>二. 分词速度：</h3>

测试环境：2.8GHZ/2G/Ubuntu

Simple 模式： 1366058字/秒 3774.5KB/秒

Complex 模式： 479338字/秒 1324.4KB/秒


<h3>分词测试:</h3>

文本一: 

歧义和同义词:研究生命起源，混合词: 做B超检查身体，x射线本质是什么，今天去奇都ktv唱卡拉ok去，哆啦a梦是一个动漫中的主角，单位和全角: 2009年８月６日开始大学之旅，岳阳今天的气温为38.6℃, 也就是101.48℉, 中文数字/分数: 你分三十分之二, 小陈拿三十分之五,剩下的三十分之二十三全部是我的，那是一九九八年前的事了，四川麻辣烫很好吃，五四运动留下的五四精神。笔记本五折包邮亏本大甩卖。人名识别: 我是陈鑫，也是jcesg的作者，三国时期的诸葛亮是个天才，我们一起给刘翔加油，罗志高兴奋极了因为老吴送了他一台笔记本。配对标点: 本次『畅想杯』黑客技术大赛的得主为电信09-2BF的张三，奖励C++程序设计语言一书和【畅想网络】的『PHP教程』一套。特殊字母: 【Ⅰ】（Ⅱ），英文数字: bug report chenxin619315@gmail.com or visit http://code.google.com/p/jcseg, we all admire the hacker spirit!特殊数字: ① ⑩ ⑽ ㈩.

分词结果:

歧义 和 同义词 :/w 研究/v 琢磨/v 研讨/v 钻研/v 生命 起源 ，/w 混合词 :/w 做 b超/n 检查 身体 ，/w x射线/n 本质 是 什么 ，/w 今天 去 奇都ktv/nz 唱 卡拉ok/nz 去 ，/w 哆啦a梦/nz 是 一个 动漫 中 的 主角 ，/w 单位 和 全角 :/w 2009年/m 8月/m 6日/m 开始 大学 之旅 ，/w 岳阳/ns 今天 的 气温 为 38.6℃/m ,/w 也就是 101.48℉/m ,/w 中文/n 国语/n 数字 //w 分数 :/w 你/r 分 三十分之二/m 2/30/m ,/w 小陈/nr 拿 三十分之五/m 5/30/m ,/w 剩下 的 三十分之二十三/m 23/30/m 全部 是 我的 ，/w 那是 一九九八年/m 1998年/m 前 的 事 了 ，/w 四川/ns 麻辣烫/n 很 好吃 ，/w 五四运动 留下 的 五四/m 54/m 精神 。/w 笔记本 五折/m 5折/m 包邮 亏本 大甩卖 甩卖 。/w 人名 识别 :/w 我/r 是 陈鑫/nr ，/w 也 是 jcesg/en 的 作者 ，/w 三国 时期 的 诸葛亮 是个 天才 ，/w 我们 一起 给 刘翔 加油 ，/w 罗志高/nr 兴奋 极了 因为 老吴/nr 送了 他/r 一台 笔记本 。/w 配对 标点 :/w 本次 『/w 畅想杯/nz 』/w 黑客 技术 大赛 的 得主 为 电信/nt 09/en -/w 2bf/en bf/en 的 张三 ，/w 奖励 c++/en 程序设计 语言 一书 和 【/w 畅想网络/nz 】/w 的 『/w PHP教程/nz 』/w 一套 。/w 特殊 字母 :/w 【/w Ⅰ/nz 】/w （/w Ⅱ/m ）/w ，/w 英文/n 英语/n 数字 :/w bug/en report/en chenxin619315@gmail.com/en chenxin/en 619315/en gmail/en com/en or/en visit/en http/en :/w //w //w code.google.com/en code/en google/en com/en //w p/en //w jcseg/en ,/w we/en all/en admire/en appreciate/en like/en love/en enjoy/en the/en hacker/en spirit/en mind/en !/w 特殊 数字 :/w ①/m ⑩/m ⑽/m ㈩/m ./w

 
<h3>三. Jcseg其他主页: </h3>
Jcseg官方主页: https://code.google.com/p/jcseg/ <br />
Jcseg开源中国: http://www.oschina.net/p/jcseg <br />
Jcseg开发帮助文档: http://git.oschina.net/lionsoul/jcseg/attach_files <br />


<h3>四. 联系作者: </h3>
作者: 狮子的魂 <br />
email: chenxin619315@gmail.com <br />
qq: 1187582057 <br />

