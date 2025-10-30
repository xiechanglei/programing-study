package io.github.xiechanglei.runnder.doc;

import io.github.xiechanglei.api.Subject;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class SubjectInfo {

    private static final String FILES_DIR = "docs";

    public final String name;
    public final String id;
    public final String path;

    public final TreeMap<LessonInfo, Set<DocumentInfo>> lessons = new TreeMap<>(Comparator.comparing(LessonInfo::title));

    public int documentCount = 0;

    public SubjectInfo(Subject subject) {
        this.name = subject.name();
        this.id = buildId(this.name);
        this.path = subject.getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + File.separator + FILES_DIR;
        this.parse();
    }

    private void parse() {
        File baseDir = new File(this.path);
        if (baseDir.exists() && baseDir.isDirectory()) {
            File[] subFiles = baseDir.listFiles();
            if (subFiles != null) {
                for (File file : subFiles) {
                    if (file.isDirectory()) {
                        loadLesson(file);
                    }
                }
            }
        }
    }

    private void loadLesson(File dir) {
        String lessonName = dir.getName();
        LessonInfo lessonInfo = new LessonInfo(buildId(lessonName), lessonName);
        Set<DocumentInfo> docs = new TreeSet<>(Comparator.comparing(DocumentInfo::title));
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".md")) {
                    DocumentInfo doc = new DocumentInfo(buildId(file.getName()),
                            file.getName().substring(0, file.getName().length() - 3), file.getName());
                    docs.add(doc);
                }
            }
        }

        if (!docs.isEmpty()) {
            lessons.put(lessonInfo, docs);
            documentCount += docs.size();
        }
    }

    public DocumentInfo findDocument(LessonInfo lesson, String documentId) {
        Set<DocumentInfo> docs = lessons.get(lesson);
        if (docs != null) {
            for (DocumentInfo doc : docs) {
                if (doc.id().equals(documentId)) {
                    return doc;
                }
            }
        }
        return null;
    }

    public LessonInfo findLessonById(String lessonId) {
        for (LessonInfo lesson : lessons.keySet()) {
            if (lesson.id().equals(lessonId)) {
                return lesson;
            }
        }
        return null;
    }


    public static MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String buildId(String name) {
        try {
            byte[] hashBytes = digest.digest(name.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString(); // 得到64位的十六进制数字字符串
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate ID", e);
        }
    }
}
