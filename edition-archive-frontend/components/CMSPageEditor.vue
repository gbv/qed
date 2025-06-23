<template>
  <editor v-model="model.content" :init="editorOptions">
  </editor>
</template>

<script lang="ts" setup>
import Editor from '@tinymce/tinymce-vue';

import {type BlobInfo, type FileUploadEvent} from "~/api/TinyMCEHelper";
import {registerEditorLink, EDITOR_LINK_ELEMENT_NAME} from "~/api/TinyMCEEditorLink";
import {registerEditorCapitalizeButton} from "~/api/TinyMCECapitalizeButton";

if (process.client) {
  await import('tinymce/tinymce');
  await Promise.all([
    import('tinymce/themes/silver'),
    import('tinymce/plugins/autoresize/plugin'),
    import('tinymce/plugins/save/plugin'),
    import('tinymce/plugins/code/plugin'),
    import('tinymce/plugins/directionality/plugin'),
    import('tinymce/plugins/fullscreen/plugin'),
    import('tinymce/plugins/hr/plugin'),
    import('tinymce/plugins/image/plugin'),
    import('tinymce/plugins/imagetools/plugin'),
    import('tinymce/plugins/insertdatetime/plugin'),
    import('tinymce/plugins/link/plugin'),
    import('tinymce/plugins/lists/plugin'),
    import('tinymce/plugins/media/plugin'),
    import('tinymce/plugins/pagebreak/plugin'),
    import('tinymce/plugins/paste/plugin'),
    import('tinymce/plugins/preview/plugin'),
    import('tinymce/plugins/table/plugin'),
    import('tinymce/icons/default')])
}


const props = defineProps(["content"]);

const emit = defineEmits(["save", "cancel", "fileUpload"]);

const i18n = useI18n();


const model = reactive({
  content: props.content,
});

const saveContent = () => {
  emit('save', model.content);
}

const cancelSave = () => {
  emit('cancel');
}

const fileUpload = (blobInfo: BlobInfo,
                    success: (url: string) => void,
                    failure: (err: string, options?: { remove?: boolean }) => void,
                    progress: (percent: number) => void) => {

  emit('fileUpload', {blobInfo, success, failure, progress} as FileUploadEvent);
}
const setup = (editor: any) => {
  registerEditorCapitalizeButton(editor, i18n);
  registerEditorLink(editor, i18n);
}


const editorOptions: Ref<any> = ref({
  "skin": false,
  "content_css": false,
  "inline": true,
  "menubar": false,
  "statusbar": false,
  "toolbar_persist": true,
  "plugins": "save media table hr lists image link pagebreak code insertdatetime autoresize paste preview fullscreen directionality",
  "toolbar": "save cancel | bold italic underline capitalize | h1 h2 h3 h4 | aligncenter alignjustify alignleft alignright alignnone | numlist bullist | removeformat blockquote | bibl image | hr code",
  "formats": {
    smallcaps: {inline: 'span', styles: {'font-variant': 'small-caps'}},
  },
  "save_onsavecallback": saveContent,
  "save_oncancelcallback": cancelSave,
  "images_upload_handler": fileUpload as any,
  "setup": setup,
  "external_plugins": {} as Record<string, string>,
  "custom_elements": "~gallia-pontificia-online-regest-bibl",
  "extended_valid_elements": "gallia-pontificia-online-editor-link[type|refe],change-language[lang]",
  "content_style": "gallia-pontificia-online-editor-link { display: inline-block; color: #0000ff; } change-language { display: inline-block; color: #0000ff; }",
  "save_enablewhendirty": false
});


</script>

<style lang="scss">
@import 'tinymce/skins/ui/oxide/skin.min.css';


</style>