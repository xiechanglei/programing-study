package io.github.xiechanglei.runnder.web;

import com.sun.net.httpserver.HttpExchange;
import io.github.xiechanglei.runnder.doc.DocumentInfo;
import io.github.xiechanglei.runnder.doc.SubjectInfo;
import io.github.xiechanglei.runnder.doc.SubjectLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
        HtmlDocumentHelper document = HtmlDocumentHelper.create("Programing Study").appendCssLink("/css/subject.css").appendCssLink("/css/base.css");
        document.appendBody("<h1>Available Subjects</h1>");
        document.appendBody("<div class='subject-list'>" + SubjectLoader.all_subjects.stream().map(subject -> "<a class='subject-block' href=\"/subject/" + subject.id + "\">" + subject.name + "</a>").collect(Collectors.joining()) + "</div>");
        byte[] bytes = document.build().getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
    };

    PageHandler CssFileHandler = (exchange, webPathDesc) -> dealResource(exchange, STATIC_RESOURCE + "css/" + webPathDesc.remainPath, "text/css");

    PageHandler JavaScriptFileHandler = (exchange, webPathDesc) -> dealResource(exchange, STATIC_RESOURCE + "js/" + webPathDesc.remainPath, "application/javascript");

    PageHandler InterViewHandler = (exchange, webPathDesc) -> {
        SubjectInfo subject;
        if (webPathDesc.pathSegments.length < 2 || (subject = parseSubject(webPathDesc)) == null) {
            PageHandler.ResourceNotFoundHandler.handle(exchange, null);
        } else {
            DocumentInfo interview = subject.findInterviewById(webPathDesc.pathSegments[1]);
            if (interview == null) {
                PageHandler.ResourceNotFoundHandler.handle(exchange, null);
            } else {
                dealAbsMdResource(exchange, interview);
            }
        }
    };

    PageHandler SubjectHandler = (exchange, webPathDesc) -> {
        SubjectInfo subject = parseSubject(webPathDesc);
        if (subject == null) {
            PageHandler.ResourceNotFoundHandler.handle(exchange, null);
        } else {
            HtmlDocumentHelper document = HtmlDocumentHelper.create(subject.name)
                    .appendCssLink("/css/base.css");
            document.appendBody("<h1>Lesson: " + subject.name + "</h1>");
            document.appendBody("<div class='lesson-list'>" + subject.lessons.stream().map(lesson -> "<a class='lesson-item' href='/lesson/" + subject.id + "/" + lesson.id + "'>" + lesson.title + "</a>").collect(Collectors.joining()) + "</div>");
            document.appendBody("<div class='interview-list'>" + subject.interviews.stream().map(interview -> "<a class='interview-item' href='/interview/" + subject.id + "/" + interview.id + "'>" + interview.title + "</a>").collect(Collectors.joining()) + "</div>");
            byte[] bytes = document.build().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
        }
    };

    private static SubjectInfo parseSubject(WebPathDesc webPathDesc) throws IOException {
        SubjectInfo subject = null;
        if (webPathDesc.pathSegments.length > 0) {
            subject = SubjectLoader.getSubjectById(webPathDesc.pathSegments[0]);
        }
        return subject;
    }

    private static void dealResource(HttpExchange exchange, String resourcePath, String contentType) throws IOException {
        URL resource = ProgramStudyWebRender.class.getClassLoader().getResource(resourcePath);
        if (resource == null) {
            PageHandler.ResourceNotFoundHandler.handle(exchange, null);
        } else {
            exchange.getResponseHeaders().add("Content-Type", contentType + "; charset=utf-8");
            exchange.sendResponseHeaders(200, resource.openConnection().getContentLength());
            try (InputStream inputStream = resource.openStream()) {
                inputStream.transferTo(exchange.getResponseBody());
            }
        }
    }

    private static void dealAbsMdResource(HttpExchange exchange, DocumentInfo doc) throws IOException {
        File file = new File(doc.path);
        if (!file.exists() || file.isDirectory()) {
            PageHandler.ResourceNotFoundHandler.handle(exchange, null);
        } else {
            try (InputStream inputStream = new FileInputStream(file)) {
                String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                HtmlDocumentHelper document = HtmlDocumentHelper.create(doc.title)
                        .appendCssLink("/css/prism.min.css")
                        .appendJsLink("/js/marked.js")
                        .appendJsLink("/js/prism/prism-core.min.js")
                        .appendJsLink("/js/prism/prism-autoloader.min.js")
                        .appendJsLink("/js/markdown-renderer.js");
                byte[] bytes = document.appendBody("<template id='md-content'>" + content + "</template>").build().getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
            }
        }
    }

    void handle(HttpExchange exchange, WebPathDesc webPathDesc) throws IOException;
}
