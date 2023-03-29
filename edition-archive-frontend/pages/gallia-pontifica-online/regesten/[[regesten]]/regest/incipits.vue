<template>
  <GalliaPontificaOnlineLayout>

    <template #content>
      <h2>{{ $t("gpo.pages.incipitIndex") }}</h2>
      <ul v-if="data" class="list-group list-group-flush mt-5">
        <li v-for="incip in data.incipitsSorted" class="list-group-item">

          {{ incip }}
          (<nuxt-link v-for="doc in data.incipitsMap.get(incip)" :key="doc.idno" :href="`/gallia-pontifica-online/regesten/${route.params.regesten}/regest/${doc.idno}`"
                     class="regest-link-seperated"
          >{{ doc.idno }}</nuxt-link>)
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
const {data, error} = await useAsyncData(`objectType:regest,incipit`, async () => {
  const request = await fetch(`${$solrURL()}main/select/?q=objectType:regest&wt=json&rows=99999&fq=initium:*&fl=initium,idno`);
  const solrResultJson = await request.json();

  const incipitsSorted = [];
  const incipitsMap = new Map();

  for (let i = 0; i < solrResultJson.response.docs.length; i++) {

    const doc = solrResultJson.response.docs[i];
    const {initium} = doc;
    for (let j = 0; j < initium.length; j++) {
      const incipit = initium[j];
      if (incipitsMap.has(incipit)) {
        incipitsMap.get(incipit).push(doc);
      } else {
        incipitsSorted.push(incipit);
        incipitsMap.set(incipit, [doc]);
      }
    }

  }

  incipitsSorted.sort();

  return {incipitsMap, incipitsSorted};
});
</script>

<style scoped>
.regest-link-seperated:not(:last-child):after {
  content: ", ";
  text-decoration: none;
}


</style>