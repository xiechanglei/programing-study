package io.github.xiechanglei.runnder;

import com.sun.net.httpserver.HttpServer;
import io.github.xiechanglei.runnder.doc.SubjectLoader;
import io.github.xiechanglei.runnder.web.ProgramStudyWebRender;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * 程序学习运行器
 *
 * @author xie
 * @since 2025/10/23
 */
public class ProgramStudyRuner {
    /**
     * 不指定端口启动服务
     */
    public static void start() {
        start(0);
    }

    /**
     * 指定端口启动服务
     */
    public static void start(int needPort) {
        try {
            SubjectLoader.loadAllSubjects();
            // 启动一个随机可用的web端口，提供web界面供用户选择学习的科目和内容
            HttpServer server = HttpServer.create(new InetSocketAddress(needPort), 0);
            // 获取端口号
            int port = server.getAddress().getPort();
            // 处理请求
            ProgramStudyWebRender.handleServer(server);
            server.start();
            System.out.println("programing study site http://127.0.0.1:" + port);
        } catch (IOException e) {
            System.out.println("start programing study runner failed:" + e.getMessage());
        }
    }

}
