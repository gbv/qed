<template>
  <GalliaPontificaOnlineLayout>
    <template #content>
      <GalliaPontificaOnlineReimser>
        <template #regesten>
          <h2>{{ $t("person_index") }}</h2>
          <ul class="list-group list-group-flush mt-5" v-if="data">
            <li v-for="doc in data.docs" class="list-group-item">
              <nuxt-link :href="`/gallia_pontifica_online/regest/suche/person/?personObj=${doc.id}`">{{ doc.displayName.join(", ") }}</nuxt-link>
            </li>
          </ul>
        </template>
      </GalliaPontificaOnlineReimser>
    </template>
  </GalliaPontificaOnlineLayout>
</template>

<script>
  export default {}
  </script>
  <script setup>
  const route = useRoute()
  const config = useRuntimeConfig()
  import {createError} from 'h3'
  import {
    XMLApi,
    findFirstElement,
    findElement,
    byName,
    and,
    or,
    byAttr,
    flattenElement,
    flattenElementExcept
  } from "~/api/XMLApi";
  const {$solrURL, $backendURL} = useNuxtApp();
  const {data, error} = await useAsyncData(`objectType:person`, async () => {
    const request = await fetch(`${$solrURL()}main/select/?q=objectType:person&wt=json&rows=99999`)
    const json = await request.json();
    if (json.response.numFound === 0) {
      throw 404;
    }
    return json.response;
  });
</script>

<style scoped></style>