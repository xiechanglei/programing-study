package io.github.xiechanglei.runnder.web;

import com.sun.net.httpserver.HttpServer;

import static io.github.xiechanglei.runnder.web.PageHandler.*;

public class ProgramStudyWebRender {

    public static void handleServer(HttpServer server) {
        server.createContext("/", exchange -> {
            String requestPath = exchange.getRequestURI().getPath();
            WebPathDesc webPathDesc = WebPathDesc.parseRequestPath(requestPath);
            PageHandler handler = switch (webPathDesc.type) {
                case "" -> RootHandler;
                case "css" -> CssFileHandler;
                case "js" -> JavaScriptFileHandler;
                case "subject" -> SubjectHandler;
                case "interview" -> InterViewHandler;
                case "lesson" -> LessonHandler;
                default -> ResourceNotFoundHandler;
            };
            handler.handle(exchange, webPathDesc);
            exchange.close();
        });
    }


}
