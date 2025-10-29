package io.github.xiechanglei.lession.typescript;

import com.google.auto.service.AutoService;
import io.github.xiechanglei.api.Subject;

/**
 * Typescript课程信息
 *
 * @author xie
 * @since 2025/10/23
 */
@AutoService(Subject.class)
public class TypescriptSubject implements Subject {
    @Override
    public String name() {
        return "Typescript";
    }
}
