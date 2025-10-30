package io.github.xiechanglei.runnder.web;

public class WebPathDesc {
    String[] pathSegments;
    String type;
    String fullPath;
    String subPath;

    public WebPathDesc(String type, String[] params) {
        this.type = type;
        this.pathSegments = params;
        this.subPath = String.join("/", params);
        this.fullPath = "/" + type + (params.length > 0 ? ("/" + this.subPath) : "");
    }


    /**
     * 对请求路径进行解析，约定请求路径格式为 /{type}/{id}/{subid}...
     *
     * @param path 请求路径
     */
    public static WebPathDesc parseRequestPath(String path) {
        String[] segments = path.split("/");
        String type = segments.length > 1 ? segments[1] : "";
        // 剩下的全部作为业务参数
        if (segments.length >= 1) {
            String[] params = new String[segments.length - 2];
            System.arraycopy(segments, 2, params, 0, segments.length - 2);
            return new WebPathDesc(type, params);
        } else {
            return new WebPathDesc(type, new String[0]);
        }
    }
}
