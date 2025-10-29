package io.github.xiechanglei.lession.golang;

import com.google.auto.service.AutoService;
import io.github.xiechanglei.api.Subject;

/**
 * Golang课程信息
 *
 * @author xie
 * @since 2025/10/23
 */
@AutoService(Subject.class)
public class GolangSubject implements Subject {
    @Override
    public String name() {
        return "Golang";
    }
}
