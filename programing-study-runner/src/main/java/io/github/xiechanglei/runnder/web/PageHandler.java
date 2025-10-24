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
        byte[] bytes = document.appendBody("<div id='pageContent'><h1>Available Subjects</h1>")
                .appendBody("<div class='subject-list'>")
                .appendBody(SubjectLoader.all_subjects.stream().map(subject -> "<a target='_blank' class='subject-block' href=\"/subject/" + subject.id + "\">" + subject.name + "<span class='subject-desc'>(lesson 21 | interview 104)</span></a>").collect(Collectors.joining()))
                .appendBody("</div>")
                .appendBody("</div>")
                .build()
                .getBytes(StandardCharsets.UTF_8);
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

    PageHandler LessonHandler = (exchange, webPathDesc) -> {
        SubjectInfo subject;
        if (webPathDesc.pathSegments.length < 2 || (subject = parseSubject(webPathDesc)) == null) {
            PageHandler.ResourceNotFoundHandler.handle(exchange, null);
        } else {
            DocumentInfo interview = subject.findLessonById(webPathDesc.pathSegments[1]);
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
            byte[] bytes = HtmlDocumentHelper.create(subject.name)
                    .appendCssLink("/css/base.css")
                    .appendJsLink("/js/subject.js")
                    .appendCssLink("/css/subject.css")
                    .appendBody("<div id='pageContent'>")
                    .appendBody("<h1>" + subject.name + "</h1>")
                    .appendBody("<div><span class='doc-tab active lesson'>Lessons</span><span class='doc-tab interview'>Interviews</span></div>")
                    .appendBody("<div class='lesson-list active'>" + subject.lessons.stream().map(lesson -> "<a target='_blank' class='lesson-item' href='/lesson/" + subject.id + "/" + lesson.id + "'>" + lesson.title + "</a>").collect(Collectors.joining()) + "</div>")
                    .appendBody("<div class='interview-list'>" + subject.interviews.stream().map(interview -> "<a target='_blank' class='interview-item' href='/interview/" + subject.id + "/" + interview.id + "'>" + interview.title + "</a>").collect(Collectors.joining()) + "</div>")
                    .appendBody("</div>")
                    .build().getBytes(StandardCharsets.UTF_8);
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
                String content = Base64.getEncoder().encodeToString(inputStream.readAllBytes());
                HtmlDocumentHelper document = HtmlDocumentHelper.create(doc.title)
                        .appendCssLink("/css/prism.min.css")
                        .appendCssLink("/css/base.css")
                        .appendCssLink("/css/doc.css")
                        .appendCssLink("/css/github-markdown.css")
                        .appendJsLink("/js/marked.js")
                        .appendJsLink("/js/prism/prism-core.min.js")
                        .appendJsLink("/js/prism/prism-autoloader.min.js")
                        .appendJsLink("/js/markdown-renderer.js");
                document.appendBody("<div id='pageContent'><div id='docBlock'><h1>" + doc.title + "</h1></div></div>");
                byte[] bytes = document.appendBody("<template id='md-content'>" + content + "</template>").build().getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
            }
        }
    }

    void handle(HttpExchange exchange, WebPathDesc webPathDesc) throws IOException;
}
