package io.github.xiechanglei.lession.javascript;

import com.google.auto.service.AutoService;
import io.github.xiechanglei.api.Subject;

/**
 * Javascript课程信息
 *
 * @author xie
 * @since 2025/10/23
 */
@AutoService(Subject.class)
public class JavascriptSubject implements Subject {
    @Override
    public String name() {
        return "Javascript";
    }
}
