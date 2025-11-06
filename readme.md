# programing-study

这是一个无聊的项目。使用maven模块化以及Java spi的机制来实现一个简单的编程学习工具。目的是不依赖于网络环境，随时随地都可以学习编程知识。

## quick start

创建一个maven项目（jdk 25+，都什么年代了，你还没有jdk25？？？），然后引入如下依赖：

```xml

<parent>
    <groupId>io.github.xiechanglei</groupId>
    <artifactId>programing-study</artifactId>
    <version>3.5.6.1</version>
</parent>

<dependicies>
<dependency>
    <groupId>io.github.xiechanglei</groupId>
    <artifactId>programing-study-lesson-java</artifactId>
</dependency>
</dependicies>
```

> 这里使用父项目的方式，只是为了不需要在下方的依赖中不用再写版本号而已。你也可以选择直接引入`programing-study-lesson-java`
> 模块，并且指定版本号。

然后在main方法中调用如下代码：

```java
import io.github.xiechanglei.runnder.ProgramStudyRuner;

void main() {
    ProgramStudyRuner.start();
}
```

程序会在一个随机的端口启动一个web服务，访问`http://localhost:端口号`即可看到学习页面。每个课程按照约定包含了一些教程和面试题。

ProgramStudyRuner.start的方法还支持传入一个int类型参数表示指定端口上启动web服务。

## provide subjects

下面是一个内部提供的教程：

- **programing-study-lesson-java** java语言基础教程
- **programing-study-lesson-rust** rust语言基础教程

## create your own subjects

由于使用了Java spi机制，你可以很方便的创建你自己的课程模块。创建一个maven模块，然后引入如下依赖：

```xml

<dependency>
    <groupId>io.github.xiechanglei</groupId>
    <artifactId>programing-study-api</artifactId>
</dependency>
<dependency>
<groupId>io.github.xiechanglei</groupId>
<artifactId>programing-study-runner</artifactId>
</dependency>
```

创建一个类实现`io.github.xiechanglei.api.Subject`接口,并且为类添加一个注解：

```java

import io.github.xiechanglei.api.Subject;
import com.google.auto.service.AutoService;

@AutoService(Subject.class)
public class YourSubject implements Subject {

    @Override
    public String name() {
        return "your subject name";
    }
}
```

这样就注册好了一个自定义的课程模块。

按照约定，**programing-study-runner**会在注册到Subject接口的类所在模块的`resources`目录下寻找如下目录结构：

- **docs** 目录 为课程的根目录,所有的资源都应该放在该目录下
- **docs** 目录下的第一级子目录为分组目录，如**lesson**和**interview**目录,子目录下存放所有的课程的markdown文档,目前仅仅支持检索第一级子目录
- 静态的资源可以放在**docs**目录下的任何位置,然后在文档中使用相对路径引用资源

由于maven的默认filter机制，可能造成你书写的文档中的内容在打包的时候被替换一些内容，建议单独配置`src/main/resources-docs`
目录，将原先放在`docs`目录下的内容放在`resources-docs`下，并且在pom.xml中禁用filter以及指定输出到docs目录：

```xml

<build>
    <resources>
        <resource>
            <directory>src/main/resources</directory>
            <filtering>true</filtering>
        </resource>
        <resource>
            <directory>src/main/resources-docs</directory>
            <filtering>false</filtering>
            <!--            指定最终打包到docs目录下，就不用重复创建docs目录了-->
            <targetPath>docs</targetPath>
        </resource>
    </resources>
</build>
```

当然，如果你的`parent`项目是`programing-study`，则不需要做上述配置，因为父项目已经做了相关配置。

```xml

<parent>
    <groupId>io.github.xiechanglei</groupId>
    <artifactId>programing-study</artifactId>
    <version>3.5.6.1</version>
</parent>
```

只是建议，如果你有更好的方式，也可以采用其他方式。

## that's all , enjoy it .

对自己的任务要求：

- 每天整理3个面试题目
- 每天整理3个课程信息