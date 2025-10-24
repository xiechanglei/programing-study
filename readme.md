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
    <artifactId>programing-study-runner</artifactId>
</dependency>

<dependency>
    <groupId>io.github.xiechanglei</groupId>
    <artifactId>programing-study-lesson-java</artifactId>
</dependency>
</dependicies>
```

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

- **lesson** 课程目录，存放课程的markdown文件
- **interview** 面试题目录，存放面试题的markdown文件

## that's all , enjoy it .