package io.github.xiechanglei.runnder.web;

import com.sun.net.httpserver.HttpExchange;
import io.github.xiechanglei.runnder.doc.DocumentInfo;
import io.github.xiechanglei.runnder.doc.LessonInfo;
import io.github.xiechanglei.runnder.doc.SubjectInfo;
import io.github.xiechanglei.runnder.doc.SubjectLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.stream.Collectors;

public interface PageHandler {
    String STATIC_RESOURCE = "statics/";

    PageHandler ResourceNotFoundHandler = (exchange, _) -> {
        String response = "404 Not Found";
        exchange.sendResponseHeaders(404, response.length());
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    };

    PageHandler RootHandler = (exchange, _) -> {
        HtmlDocumentHelper document = HtmlDocumentHelper.create("Programing Study").appendCssLink("/css/index.css").appendCssLink("/css/base.css");
        byte[] bytes = document.appendBody("<div id='pageContent'><h1 class='pro-title'>Available Subjects</h1>")
                .appendBody("<div class='subject-list'>")
                .appendBody(SubjectLoader.all_subjects.stream().map(subject -> "<a target='_blank' class='subject-block btn' href=\"/" + subject.id + "\">" + subject.name + "<span class='subject-desc'>(lesson " + subject.lessons.size() + " | document " + subject.documentCount + ")</span></a>").collect(Collectors.joining()))
                .appendBody("</div>")
                .appendBody("</div>")
                .build()
                .getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
    };

    PageHandler StaticResourceHandler = (exchange, webPathDesc) -> dealResource(exchange, STATIC_RESOURCE + webPathDesc.fullPath);

    PageHandler SubjectHandler = (exchange, webPathDesc) -> {
        SubjectInfo subject = SubjectLoader.getSubjectById(webPathDesc.type);
        if (subject == null) {
            PageHandler.ResourceNotFoundHandler.handle(exchange, null);
            return;
        }
        if (webPathDesc.pathSegments.length == 0) {
            handleSubjectInfo(exchange, subject);
        } else {
            handleSubjectResource(exchange, webPathDesc, subject);
        }
    };

    static void handleSubjectInfo(HttpExchange exchange, SubjectInfo subject) throws IOException {
        HtmlDocumentHelper documentHelper = HtmlDocumentHelper.create(subject.name)
                .appendCssLink("/css/base.css")
                .appendJsLink("/js/subject.js")
                .appendCssLink("/css/subject.css")
                .appendBody("<div id='pageContent'>")
                .appendBody("<h1>" + subject.name + "</h1>");
        String lessonElements = subject.lessons.keySet().stream().map(lesson -> "<span class='lesson-tab' lesson='" + lesson.id() + "'>" + lesson.title() + "</span>").collect(Collectors.joining());
        documentHelper.appendBody("<div>" + lessonElements + "</div>");
        subject.lessons.forEach((lessonId, lesson) -> {
            documentHelper.appendBody("<div class='lesson-list' lesson='" + lessonId.id() + "'>" + lesson.stream().map(doc -> "<a target='_blank' class='lesson-item btn' href='/" + subject.id + "/" + lessonId.id() + "/" + doc.id() + "'>" + doc.title() + "</a>").collect(Collectors.joining()) + "</div>");
        });
        documentHelper.appendBody("</div>");
        byte[] bytes = documentHelper.build().getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
    }

    static void handleSubjectResource(HttpExchange exchange, WebPathDesc webPathDesc, SubjectInfo subject) throws IOException {
        // 优先转化第一级子目录是课程名称.第二级目录是文档名称的资源
        LessonInfo lesson = subject.findLessonById(webPathDesc.pathSegments[0]);
        DocumentInfo document = null;
        if (lesson != null) {
            webPathDesc.pathSegments[0] = lesson.title();
            document = subject.findDocument(lesson, webPathDesc.pathSegments[1]);
            if (document != null) {
                webPathDesc.pathSegments[1] = document.originalTitle();
            }
        }
        String resourcePath = subject.path + File.separator + String.join(File.separator, webPathDesc.pathSegments);
        if (webPathDesc.pathSegments.length == 2 && document != null) {
            dealAbsMdResource(exchange, resourcePath, document.title());
        } else {
            dealAbsResource(exchange, resourcePath);
        }
    }


    private static void dealResource(HttpExchange exchange, String resourcePath) throws IOException {
        URL resource = ProgramStudyWebRender.class.getClassLoader().getResource(resourcePath);
        if (resource == null) {
            PageHandler.ResourceNotFoundHandler.handle(exchange, null);
        } else {
            exchange.getResponseHeaders().add("Content-Type", getContentType(resourcePath));
            exchange.getResponseHeaders().add("Cache-Control", "public, max-age=86400");
            exchange.sendResponseHeaders(200, resource.openConnection().getContentLength());
            try (InputStream inputStream = resource.openStream()) {
                inputStream.transferTo(exchange.getResponseBody());
            }
        }
    }

    private static void dealAbsResource(HttpExchange exchange, String resourcePath) throws IOException {
        File file = new File(resourcePath);
        if (!file.exists() || file.isDirectory()) {
            PageHandler.ResourceNotFoundHandler.handle(exchange, null);
        } else {
            exchange.getResponseHeaders().add("Content-Type", getContentType(resourcePath));
            exchange.getResponseHeaders().add("Cache-Control", "public, max-age=86400");
            exchange.sendResponseHeaders(200, file.length());
            try (InputStream inputStream = new FileInputStream(file)) {
                inputStream.transferTo(exchange.getResponseBody());
            }
        }
    }

    private static void dealAbsMdResource(HttpExchange exchange, String resourcePath, String title) throws IOException {
        File file = new File(resourcePath);
        if (!file.exists() || file.isDirectory()) {
            PageHandler.ResourceNotFoundHandler.handle(exchange, null);
        } else {
            try (InputStream inputStream = new FileInputStream(file)) {
                String content = Base64.getEncoder().encodeToString(inputStream.readAllBytes());
                HtmlDocumentHelper document = HtmlDocumentHelper.create(title)
                        .appendCssLink("/css/prism.min.css")
                        .appendCssLink("/css/base.css")
                        .appendCssLink("/css/doc.css")
                        .appendCssLink("/css/github-markdown.css")
                        .appendJsLink("/js/marked.js")
                        .appendJsLink("/js/prism/prism-core.min.js")
                        .appendJsLink("/js/prism/prism-autoloader.min.js")
                        .appendJsLink("/js/markdown-renderer.js");
                document.appendBody("<div id='pageContent'><div id='docBlock'><h1>" + title + "</h1></div></div>");
                byte[] bytes = document.appendBody("<template id='md-content'>" + content + "</template>").build().getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
            }
        }
    }

    private static String getContentType(String resourcePath) {
        return switch (resourcePath.toLowerCase().substring(resourcePath.lastIndexOf('.') + 1)) {
            case "css" -> "text/css; charset=utf-8";
            case "js" -> "application/javascript; charset=utf-8";
            case "png" -> "image/png";
            case "jpg", "jpeg" -> "image/jpeg";
            case "gif" -> "image/gif";
            case "svg" -> "image/svg+xml; charset=utf-8";
            case "html" -> "text/html; charset=utf-8";
            default -> "application/octet-stream";
        };
    }

    void handle(HttpExchange exchange, WebPathDesc webPathDesc) throws IOException;
}
