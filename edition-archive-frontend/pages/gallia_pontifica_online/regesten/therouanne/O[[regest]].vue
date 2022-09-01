<template>
  <GalliaPontificaOnlineLayout>
    <template #content>
      <GalliaPontificaOnlineReimser>
        <template #regesten>
          <client-only placeholder="Loading...">
            <XSLTComponent :document="data['regest.xml']" :xslt="xsltString"/>
          </client-only>
        </template>
      </GalliaPontificaOnlineReimser>
    </template>
  </GalliaPontificaOnlineLayout>
</template>

<script setup>
  import XSLTComponent from "../../../../components/XSLTComponent";

  const route = useRoute()
  const config = useRuntimeConfig()
  import {createError} from 'h3'
  import xsltString from '~/assets/xsl/cei2html.xsl?raw'

  const regestedIdno = route.params.regest;

  const {$solrURL, $backendURL} = useNuxtApp();

  const {data, error} = await useAsyncData(`idno:${regestedIdno}`, async () => {
    const request = await fetch(`${$solrURL()}main/select/?q=idno:${regestedIdno}&wt=json`)
    const json = await request.json();
    if (json.response.numFound === 0) {
      throw 404;
    }
    return json.response.docs[0];
  })

  if (error.value) {
    if (error.value === 404) {
      throwError(
          createError({
            statusCode: 404,
            statusMessage: 'Not Found',
          })
      );
    }
  }
</script>

<style scoped></style>