package io.github.xiechanglei.api;

/**
 * 课程信息,一些通用的约定与默认实现
 * 1.  resources 目录下的interviews文件夹中存放该课程的所有面试题
 * 2. 每个面试题使用单独的文件进行存放，文件命名规则为：001_面试题标题.md
 * 3. 面试题文件内容使用Markdown格式进行编写
 * 4. resources 目录下的lessons文件夹中存放该课程的所有学习资料
 * 5. 每个学习资料使用单独的文件进行存放，文件命名规则为：001_资料标题.md
 *
 * @author xie
 * @date 2025/10/23
 */
public interface Subject {
    /**
     * 课程的名称
     */
    String name();
}
