package io.github.xiechanglei.runnder.doc;

import io.github.xiechanglei.api.Subject;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * 类的详细说明
 *
 * @author xie
 * @since 2025/10/23
 */
public class SubjectInfo {

    public final String name;
    public final String id;

    public List<DocumentInfo> interviews;
    public List<DocumentInfo> lessons;

    public SubjectInfo(Subject subject) {
        this.name = subject.name();
        this.id = buildId(this.name);
        String basePath = subject.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        interviews = loadDocuments(basePath, "interview");
        lessons = loadDocuments(basePath, "lesson");
    }

    public DocumentInfo findInterviewById(String id) {
        for (DocumentInfo doc : interviews) {
            if (doc.id.equals(id)) {
                return doc;
            }
        }
        return null;
    }

    public DocumentInfo findLessonById(String id) {
        for (DocumentInfo doc : lessons) {
            if (doc.id.equals(id)) {
                return doc;
            }
        }
        return null;
    }

    private static List<DocumentInfo> loadDocuments(String basePath, String dir) {
        List<DocumentInfo> documents = new ArrayList<>();
        File interviewDir = new File(basePath + File.separator + dir);
        if (interviewDir.exists() && interviewDir.isDirectory()) {
            File[] files = interviewDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    DocumentInfo doc = new DocumentInfo();
                    // todo 多级目录
                    doc.id = buildId(file.getName());
                    doc.title = file.getName();
                    if (doc.title.toLowerCase().endsWith(".md")) {
                        doc.title = doc.title.substring(0, doc.title.length() - 3);
                    }
                    doc.path = file.getAbsolutePath();
                    documents.add(doc);
                }
            }
        }
        return documents;
    }

    private static String buildId(String name) {
        return Base64.getEncoder().encodeToString(name.getBytes());
    }
}
