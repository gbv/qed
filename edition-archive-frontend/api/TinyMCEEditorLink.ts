export const EDITOR_LINK_ELEMENT_NAME = "gallia-pontifica-online-editor-link";
export const registerEditorLink = (editor:any, i18n:any) => {

  const biblButton = editor.ui.registry.addButton('bibl', {
    text: i18n.t('cms_page_editor_bibl'),
    onAction: () => {

      const selection = editor?.selection?.getNode();
      const [refe, type] = [selection?.getAttribute('refe') || "", selection.getAttribute('type') || "external"];
      const dialog = editor.windowManager.open({
        title: i18n.t("cms_page_editor_bibl_dialog_title"),
        body: {
          type: 'panel',
          items: [
            {
              type: 'selectbox',
              name: 'type',
              label: i18n.t("cms_page_editor_bibl_dialog_type"),
              items: [
                {text: i18n.t("cms_page_editor_bibl_dialog_type_external"), value: 'external'},
                {text: i18n.t("cms_page_editor_bibl_dialog_type_dekretale"), value: 'dekretale'},
                {text: i18n.t("cms_page_editor_bibl_dialog_type_source"), value: 'source'},
                {text: i18n.t("cms_page_editor_bibl_dialog_type_person"), value: 'person'},
                {text: i18n.t("cms_page_editor_bibl_dialog_type_place"), value: 'place'},
              ]
            },
            {
              type: 'input',
              name: 'refe',
              label: i18n.t("cms_page_editor_bibl_dialog_refe"),
            },
          ]
        },
        buttons: [
          {
            type: 'cancel',
            name: 'cancel',
            text: i18n.t("cms_page_editor_bibl_dialog_cancel"),
          },
          {
            type: 'submit',
            name: 'save',
            text: i18n.t("cms_page_editor_bibl_dialog_save"),
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
            editor.insertContent(`<gallia-pontifica-online-editor-link type="${data.type}" refe="${data.refe}">${i18n.t('cms_page_editor_bibl_defaultText')}</gallia-pontifica-online-editor-link>`);
          } else {
            const content = editor.selection.getContent();
            editor.selection.setContent(`<gallia-pontifica-online-editor-link type="${data.type}" refe="${data.refe}">${content}</gallia-pontifica-online-editor-link>`);
          }

          dialogApi.close();
        }
      });
      dialog.setData({refe, type});
    }

  });

}