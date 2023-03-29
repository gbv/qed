export const registerEditorCapitalizeButton = (editor:any, i18n:any) => {
  const capitalizeButton = editor.ui.registry.addButton('capitalize', {
    text: i18n.t('cms.page.editor.capitalize'),
    onAction: () => {
      // transform to capitalized span
      editor.execCommand('mceToggleFormat', false, 'smallcaps');
    }
  });
}
