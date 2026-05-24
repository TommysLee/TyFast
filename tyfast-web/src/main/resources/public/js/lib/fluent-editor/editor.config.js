class TinyEditor {
  static version = '3.25.11';

  // 自定义工具栏
  static TOOLBAR_CONFIG = [
    ['undo', 'redo', 'clean', 'format-painter'],
    [
      { header: [1, 2, 3, 4, 5, 6, false] },
      { font: ['songti', 'yahei', 'kaiti', 'heiti', 'lishu', 'mono', 'arial', 'arialblack', 'comic', 'impact', 'times'] },
      { size: ['12px', '14px', '16px', '18px', '20px', '24px', '32px', '36px', '48px', '72px'] },
      { lineheight: ['1', '1.2', '1.5', '1.75', '2', '3', '4', '5'] },
    ],
    ['bold', 'italic', 'strike', 'underline', 'divider'],
    [{ color: [] }, { background: [] }],
    [{ align: '' }, { align: 'center' }, { align: 'right' }, { align: 'justify' }],
    [{ list: 'bullet' }],
    [{ script: 'sub' }, { script: 'super' }],
    [{ indent: '-1' }, { indent: '+1' }],
    [{ direction: 'rtl' }],
    ['link', 'blockquote', 'code', 'code-block'],
    ['image', 'better-table'],
    ['emoji', 'video', 'formula', 'fullscreen']
  ];

  /*
   * 初始化编辑器
   */
  static init(id, option) {
    const modules = Object.assign({}, {
      toolbar: this.TOOLBAR_CONFIG,
      file: true,
      'emoji-toolbar': true,
      counter: false,
      i18n: {
        lang: 'zh-CN'
      }
    }, option || {});

    return new FluentEditor(id, {
      theme: 'snow',
      modules
    })
  }

  /*
   * 解析富文本中的LaTex公式并渲染
   */
  static resolveHtml(html) {
    if (html) {
      const parser = new DOMParser();
      const doc = parser.parseFromString(html, "text/html");
      const formulaNodes = doc.querySelectorAll(".ql-formula");
      for (let el of formulaNodes) {
        let latex = el.innerText;
        if (latex && latex?.length > 2) {
          latex = latex.substring(1, latex.length - 1);
          el.setAttribute('data-value', latex);
          el.innerHTML = katex.renderToString(latex, {
            throwOnError: false
          });
        }
      }
      html = doc.body.innerHTML;
    }
    return html || '';
  }

  /*
   * 设置编辑器内容（回显内容）
   */
  static setContent(editor, html) {
    editor && editor.clipboard.dangerouslyPasteHTML(TinyEditor.resolveHtml(html))
  }
}