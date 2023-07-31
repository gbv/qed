export const EDITOR_LINK_ELEMENT_NAME = "gallia-pontificia-online-editor-link";
export const registerEditorLink = (editor:any, i18n:any) => {

  const biblButton = editor.ui.registry.addButton('bibl', {
    text: i18n.t('cms.page.editor.bibl.label'),
    onAction: () => {

      const selection = editor?.selection?.getNode();
      const [refe, type] = [selection?.getAttribute('refe') || "", selection.getAttribute('type') || "external"];
      const dialog = editor.windowManager.open({
        title: i18n.t("cms.page.editor.bibl.dialog.title"),
        body: {
          type: 'panel',
          items: [
            {
              type: 'selectbox',
              name: 'type',
              label: i18n.t("cms.page.editor.bibl.dialog.type.label"),
              items: [
                {text: i18n.t("cms.page.editor.bibl.dialog.type.external"), value: 'external'},
                {text: i18n.t("cms.page.editor.bibl.dialog.type.external"), value: 'internal'},
                {text: i18n.t("cms.page.editor.bibl.dialog.type.dekretale"), value: 'dekretale'},
                {text: i18n.t("cms.page.editor.bibl.dialog.type.source"), value: 'source'},
                {text: i18n.t("cms.page.editor.bibl.dialog.type.person"), value: 'person'},
                {text: i18n.t("cms.page.editor.bibl.dialog.type.place"), value: 'place'},
              ]
            },
            {
              type: 'input',
              name: 'refe',
              label: i18n.t("cms.page.editor.bibl.dialog.reference"),
            },
          ]
        },
        buttons: [
          {
            type: 'cancel',
            name: 'cancel',
            text: i18n.t("cms.page.editor.bibl.dialog.cancel"),
          },
          {
            type: 'submit',
            name: 'save',
            text: i18n.t("cms.page.editor.bibl.dialog.save"),
            primary: true
          }
        ],
        onSubmit: (dialogApi: any) => {
          const data = dialogApi.getData();
          const selection = editor?.selection?.getNode();
          const [refe, type] = [selection?.getAttribute('refe') || "", selection.getAttribute('type') || "external"];
          const currentNode = editor?.selection?.getNode();
          debugger;
          if (currentNode?.nodeName?.toLowerCase() == EDITOR_LINK_ELEMENT_NAME) {
            currentNode.setAttribute("type", data.type);
            currentNode.setAttribute("refe", data.refe);
          } else if (editor.selection.getContent() == "") {
            editor.insertContent(`<gallia-pontificia-online-editor-link type="${data.type}" refe="${data.refe}">${i18n.t('cms.page.editor.bibl.defaultText')}</gallia-pontificia-online-editor-link>`);
          } else {
            const content = editor.selection.getContent();
            editor.selection.setContent(`<gallia-pontificia-online-editor-link type="${data.type}" refe="${data.refe}">${content}</gallia-pontificia-online-editor-link>`);
          }

          dialogApi.close();
        }
      });
      dialog.setData({refe, type});
    }

  });

}