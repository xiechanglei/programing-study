package io.github.xiechanglei.runnder.doc;

import io.github.xiechanglei.api.Subject;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * subject加载器,使用ServiceLoader机制加载所有的Subject实现
 *
 * @author xie
 * @since 2025/10/23
 */
public class SubjectLoader {
    public static List<SubjectInfo> all_subjects = new ArrayList<>();

    /**
     * 加载所有的Subject实现
     */
    public static void loadAllSubjects() {
        all_subjects = new ArrayList<>();
        ServiceLoader<Subject> loader = ServiceLoader.load(Subject.class);
        for (Subject subject : loader) {
            all_subjects.add(new SubjectInfo(subject));
        }
    }

    public static SubjectInfo getSubjectById(String id) {
        for (SubjectInfo subject : all_subjects) {
            if (subject.id.equals(id)) {
                return subject;
            }
        }
        return null;
    }
}
