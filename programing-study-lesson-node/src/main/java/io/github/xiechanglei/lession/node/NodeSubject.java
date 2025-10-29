package io.github.xiechanglei.lession.node;

import com.google.auto.service.AutoService;
import io.github.xiechanglei.api.Subject;

/**
 * Node课程信息
 *
 * @author xie
 * @since 2025/10/23
 */
@AutoService(Subject.class)
public class NodeSubject implements Subject {
    @Override
    public String name() {
        return "Css";
    }
}
