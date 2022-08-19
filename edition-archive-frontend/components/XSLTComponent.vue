<template>
  <section v-html="model.html">

  </section>
</template>

<script setup>

const props = defineProps(['xslt', 'document'])
const model = reactive({
  html: ""
})

if(!process.server){
  const xsltProcessor = new XSLTProcessor();
  const parser = new DOMParser();
  const xslt = parser.parseFromString(props.xslt, "text/xml");
  const doc = parser.parseFromString(props.document, "text/xml");

  xsltProcessor.importStylesheet(xslt.children[0]);
  const result = xsltProcessor.transformToDocument(doc.children[0]);
  model.html = result.body.outerHTML;
}

</script>

<style scoped>

</style>