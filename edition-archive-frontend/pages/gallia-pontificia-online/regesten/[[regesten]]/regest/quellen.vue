<template>
  <GalliaPontificiaOnlineLayout>

    <template #content>
      <h3>{{ $t("gpo.pages.sourceIndex") }}</h3>
      <ul v-if="data" class="list-group list-group-flush mt-5">
        <li v-for="doc in data" :id="doc['identifier.key'][0]" :class="highlight===doc.id?'text-secondary':''"
            class="list-group-item">
          <GalliaPontificiaOnlineSource :source="doc" :hide-index-link="true" />
        </li>
      </ul>
    </template>

    <template #menu>
      <GalliaPontificiaOnlineMenu/>
    </template>

  </GalliaPontificiaOnlineLayout>
</template>


<script lang="ts" setup>
const route = useRoute()
const config = useRuntimeConfig()
const highlight = computed(() => route.hash.substring(1));

const {$solrURL, $backendURL} = useNuxtApp();
const {data, error} = await useAsyncData(`objectType:source`, async () => {
  const request = await fetch(`${$solrURL()}main/select/?q=objectType:source&wt=json&rows=99999`);
  const json = await request.json();

  if (json.response.numFound === 0) {
    throw 404;
  }
  return json.response.docs.sort((a:any,b:any) => a.shortTitle.localeCompare(b.shortTitle));
});

onMounted(()=>{
  if (highlight.value !== '') {
    const elPresentInterval = window.setInterval(() => {
      const el = document.getElementById(highlight.value);
      if (el) {
        window.clearInterval(elPresentInterval);
        window.scrollTo({top: el.offsetTop})
      }
    }, 200);
  }
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