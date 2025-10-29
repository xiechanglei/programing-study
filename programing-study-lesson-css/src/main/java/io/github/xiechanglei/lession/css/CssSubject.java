package io.github.xiechanglei.lession.css;

import com.google.auto.service.AutoService;
import io.github.xiechanglei.api.Subject;

/**
 * Css课程信息
 *
 * @author xie
 * @since 2025/10/23
 */
@AutoService(Subject.class)
public class CssSubject implements Subject {
    @Override
    public String name() {
        return "Css";
    }
}
