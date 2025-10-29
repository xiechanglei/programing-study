package io.github.xiechanglei.lession.cpp;

import com.google.auto.service.AutoService;
import io.github.xiechanglei.api.Subject;

/**
 * C++课程信息
 *
 * @author xie
 * @since 2025/10/23
 */
@AutoService(Subject.class)
public class CppSubject implements Subject {
    @Override
    public String name() {
        return "C++";
    }
}
