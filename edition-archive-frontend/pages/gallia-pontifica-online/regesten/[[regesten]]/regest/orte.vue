<template>
  <GalliaPontificaOnlineLayout>

    <template #content>
      <h3>{{ $t("gpo.pages.placeIndex") }}</h3>
      <ul class="list-group list-group-flush no-underline" v-if="data">
        <li v-for="doc in data" :id="doc.id" :class="highlight===doc.id?'text-secondary':''"
            class="list-group-item">
          <GalliaPontificaOnlinePlace v-if="doc.objectType=='place'" :placeId="doc.id" :skipDisplayName="true">
            {{ doc.displayName }} <i v-if="hasExternalIdentifier(doc) || hasKey(doc)"
                                                :class="hasExternalIdentifier(doc) ? 'text-secondary':''"
                                                class="bi bi-link-45deg"></i>
          </GalliaPontificaOnlinePlace>
          <GalliaPontificaOnlineOrganization v-else :organizationId="doc.id" :skipDisplayName="true">
            {{ doc.displayName }} <i v-if="hasExternalIdentifier(doc) || hasKey(doc)"
                                                :class="hasExternalIdentifier(doc) ? 'text-secondary':''"
                                                class="bi bi-link-45deg"></i>
          </GalliaPontificaOnlineOrganization>
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

const highlight = computed(() => route.hash.substring(1));

const hasExternalIdentifier = (doc: any) => {
  return doc['identifier.VIAF'] || doc['identifier.GEONAMES'];
}

const hasKey = (doc: any) => {
  return doc['identifier.key'];
}

const {$solrURL, $backendURL} = useNuxtApp();
const {data, error} = await useAsyncData(`objectType:place or objectType:organization`, async () => {
  const request = await fetch(`${$solrURL()}main/select/?q=objectType:place%20OR%20objectType:organization&wt=json&rows=99999`)
  const json = await request.json();
  if (json.response.numFound === 0) {
    throw 404;
  }

  const docs = json.response.docs;
  docs.sort((a: any, b: any) => {
    return a.displayName.localeCompare(b.displayName);
  });

  return docs;
});

onMounted(()=>{
  if (process?.client && highlight.value!=='') {
    const elPresentInterval = window.setInterval(() => {
      const el = document.getElementById(highlight.value);

      if (el) {
        window.clearInterval(elPresentInterval);
        let count = 0;
        const interval = setInterval(() => {
          if (count > 2) {
            clearInterval(interval);
          }

          if(el.classList.contains("bg-secondary")){
            el.classList.remove("bg-secondary");
          } else {
            el.classList.add("bg-secondary");
          }
          count++;
        }, 300);
      }
    }, 200);
  }
});
</script>

<style scoped></style>