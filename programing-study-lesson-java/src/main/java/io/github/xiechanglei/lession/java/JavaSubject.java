package io.github.xiechanglei.lession.java;

import com.google.auto.service.AutoService;
import io.github.xiechanglei.api.Subject;

/**
 * java课程信息
 *
 * @author xie
 * @since 2025/10/23
 */
@AutoService(Subject.class)
public class JavaSubject implements Subject {
    @Override
    public String name() {
        return "Java";
    }
}