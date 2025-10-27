package io.github.xiechanglei.runnder.doc;

import io.github.xiechanglei.api.Subject;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
        interviews.sort((a, b) -> a.title.compareToIgnoreCase(b.title));
        lessons.sort((a, b) -> a.title.compareToIgnoreCase(b.title));
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
