<template>
  <GalliaPontificaOnlineLayout>

    <template #content>
      <h3>{{ $t("gpo.pages.sourceIndex") }}</h3>
      <ul v-if="data" class="list-group list-group-flush mt-5">
        <li v-for="doc in data.docs" :id="doc['identifier.key']" class="list-group-item">
          <GalliaPontificaOnlineSource :source="doc" />
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
const {data, error} = await useAsyncData(`objectType:source`, async () => {
  const request = await fetch(`${$solrURL()}main/select/?q=objectType:source&wt=json&rows=99999&sort=shortTitle%20asc`);
  const json = await request.json();
  if (json.response.numFound === 0) {
    throw 404;
  }
  return json.response;
});
</script>

<style scoped>

.link-list a {
  margin-right: 1em;
  padding-left: 1em;
  border-left: 1px solid rgba(0, 0, 0, 0.125);;
}

.link-list a:first-child{
  padding-left: 0px;
  border-left: none;
}


</style>