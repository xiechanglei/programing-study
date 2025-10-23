package io.github.xiechanglei.runnder;

import io.github.xiechanglei.api.Subject;

import java.util.ServiceLoader;

/**
 * 类的详细说明
 *
 * @author xie
 * @date 2025/10/23
 */
public class SubjectLoader {
    static {
        ServiceLoader<Subject> loader = ServiceLoader.load(Subject.class);
        for (Subject subject : loader) {
            System.out.println("加载课程：" + subject.name());
        }
    }
}
