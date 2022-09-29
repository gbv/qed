<template>
  <GalliaPontificaOnlineLayout>

    <template #content>
          <h3>{{ $t("person_index") }}</h3>
          <ul class="list-group list-group-flush no-underline" v-if="data">
            <li v-for="doc in data.docs" class="list-group-item">
              <nuxt-link :href="`/gallia-pontifica-online/regesten/${route.params.regesten}/regest/suche/person?personObj=${doc.id}`">{{ doc.displayName.join(", ") }}</nuxt-link>
            </li>
          </ul>
    </template>

    <template #menu>
      <GalliaPontificaOnlineMenu/>
    </template>

  </GalliaPontificaOnlineLayout>
</template>


<script lang="ts" setup>
const route = useRoute()
const config = useRuntimeConfig()

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