<template>
  <GalliaPontificaOnlineLayout>

    <template #content>
          <h2>{{ $t("incipit_index") }}</h2>
          <ul class="list-group list-group-flush mt-5" v-if="data">
            <li v-for="group in data.grouped['initium.facet'].groups" class="list-group-item">
              <template v-if="group.doclist.numFound > 1">
                {{ group.groupValue }}
                (<nuxt-link v-for="doc in group.doclist.docs"
                           :href="`/gallia-pontifica-online/regesten/${route.params.regesten}/regest/${doc.idno}`"
                >{{ doc.idno }}<template v-if="doc.idno !== group.doclist.docs[group.doclist.docs.length - 1].idno">, </template></nuxt-link>)
              </template>
             <nuxt-link :href="`/gallia-pontifica-online/regesten/${route.params.regesten}/regest/${group.doclist.docs[0].idno}`" v-else>
               {{ group.groupValue }}
              </nuxt-link>
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
  const {data, error} = await useAsyncData(`objectType:regest`, async () => {
    const request = await fetch(`${$solrURL()}main/select/?q=objectType:regest&wt=json&rows=99999&fq=initium:*&group=true&group.field=initium.facet&group.limit=99&sort=initium.facet%20asc`)
    const json = await request.json();

    return json;
  });
</script>

<style scoped></style>