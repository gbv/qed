<template>
  <GalliaPontificaOnlineLayout>

    <template #content>
      <h3>{{ $t("manuscript_index") }}</h3>
      <div v-if="data">
        <article v-for="doc in data.docs" class="card mt-2 mb-2" :id="doc['identifier.key']">
          <div class="card-body">
            <GalliaPontificaOnlineManuscript :manuscript="doc" />
          </div>
        </article>
      </div>
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
const {data, error} = await useAsyncData(`objectType:manuscript`, async () => {
  const request = await fetch(`${$solrURL()}main/select/?q=objectType:manuscript&wt=json&rows=99999&sort=shelfmark%20asc`)
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