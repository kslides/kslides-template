MarkdownSlides can use external content by specifying a filename (see: https://revealjs.com/markdown/#external-markdown)
This directory is for files that you want to serve up via HTTP.

markdownSlide {
  filename = markdown.md
}

This slide would retrieve markdown.md from this folder. Note: slides using external content
will not work if the slides are served as static html files.