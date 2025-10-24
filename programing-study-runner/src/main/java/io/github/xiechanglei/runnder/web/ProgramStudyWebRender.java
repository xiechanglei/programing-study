package io.github.xiechanglei.runnder.web;

import com.sun.net.httpserver.HttpServer;

import static io.github.xiechanglei.runnder.web.PageHandler.*;

/**
 * 文档的web渲染处理器，负责将文档内容渲染为网页
 */
public class ProgramStudyWebRender {

    /**
     * 定义web服务器的请求处理逻辑。包含一些路由规则
     * @param server http服务器实例
     */
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
