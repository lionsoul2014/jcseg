# 如何ANT 编译 jcseg：

## 一，lucene 4.10.4支持：

在该目录下放入如下jar文件即可：

*1. elasticsearch-1.7.3.jar
* 2. lucene-analyzers-common-4.10.4.jar
* 3. lucene-core-4.10.4.jar
* 4. jetty-server-9.3.0.RC0.jar,jetty-util-9.3.0.RC0.jar
* 5. javax.servlet-api-3.1.0.jar

## 二，lucene 5.1.0支持：

在该目录下放入如下jar文件即可：

* 1. elasticsearch-1.7.3.jar
* 2. lucene-analyzers-common-5.1.0.jar
* 3. lucene-core-5.1.0.jar
* 4. jetty-server-9.3.0.RC0.jar,jetty-util-9.3.0.RC0.jar
* 5. javax.servlet-api-3.1.0.jar

ant all编译即可

## 三，lucene 6.0.x, elasticsearch-2.3.1：

在该目录下放入如下jar文件即可：

* 1. elasticsearch-2.3.1.jar
* 2. lucene-analyzers-common-6.0.0.jar
* 3. lucene-core-6.0.0.jar
* 4. jetty-server-9.3.0.RC0.jar,jetty-util-9.3.0.RC0.jar
* 5. javax.servlet-api-3.1.0.jar

## 四，其他版本lucene：

查看对应模块下的pom.xml获取对应依赖包的版本！
