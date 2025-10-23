// init marked
marked.setOptions({
    highlight: function (code, lang) {
        if (Prism.languages[lang]) {
            return Prism.highlight(code, Prism.languages[lang], lang);
        } else {
            return code;
        }
    }
});
// init prism   https://www.jsdelivr.com/package/npm/prismjs?tab=files&path=components
Prism.plugins.autoloader.languages_path = "/js/prism/";
const markdownContent = document.getElementById("md-content");
if (markdownContent) {
    const markdownBody = document.createElement("div")
    markdownBody.classList.add("markdown-body")
    markdownBody.innerHTML = marked.parse(markdownContent.innerHTML)
    document.body.append(markdownBody)
    markdownContent.remove()
}
