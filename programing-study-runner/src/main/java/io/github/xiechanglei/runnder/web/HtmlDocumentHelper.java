package io.github.xiechanglei.runnder.web;

public class HtmlDocumentHelper {
    private final StringBuilder headerHtml = new StringBuilder();
    private final StringBuilder bodyHtml = new StringBuilder();
    private final StringBuilder footerHtml = new StringBuilder();

    public HtmlDocumentHelper appendHeader(String html) {
        headerHtml.append(html);
        return this;
    }

    public HtmlDocumentHelper appendCssLink(String link) {
        headerHtml.append("<link rel=\"stylesheet\" href=\"").append(link).append("\">");
        return this;
    }

    public HtmlDocumentHelper appendJsLink(String link) {
        footerHtml.append("<script src=\"").append(link).append("\" type='module'></script>");
        return this;
    }

    public HtmlDocumentHelper appendBody(String html) {
        bodyHtml.append(html);
        return this;
    }

    public HtmlDocumentHelper appendFooter(String html) {
        footerHtml.append(html);
        return this;
    }

    public String build() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                headerHtml +
                "</head>\n" +
                "<body>\n" +
                bodyHtml +
                "</body>\n" +
                footerHtml +
                "</html>";
    }


    public static HtmlDocumentHelper create(String title) {
        return new HtmlDocumentHelper().appendHeader("<title>" + title + "</title>");
    }
}
