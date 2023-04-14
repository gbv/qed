<template>
  <GalliaPontificaOnlineLayout>

    <template #content>
          <h3>{{ $t("gpo.pages.personIndex") }}</h3>
          <ul class="list-group list-group-flush no-underline" v-if="data">
            <li v-for="doc in data" :id="doc.id" :class="highlight===doc.id?'text-secondary':''"
                class="list-group-item">
              <GalliaPontificaOnlinePerson :personId="doc.id" :skipDisplayName="true">
                {{ doc.displayName }} <i v-if="hasExternalIdentifier(doc) || hasKey(doc)"
                                                    :class="hasExternalIdentifier(doc) ? 'text-secondary':''"
                                                    class="bi bi-link-45deg"></i>
              </GalliaPontificaOnlinePerson>
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

const {$solrURL, $backendURL} = useNuxtApp();

const hasExternalIdentifier = (doc: any) => {
  return doc['identifier.VIAF'] || doc['identifier.GND'] || doc['identifier.IDREF'];
}

const hasKey = (doc: any) => {
  return doc['identifier.key'];
}

const {data, error} = await useAsyncData(`objectType:person`, async () => {
  const request = await fetch(`${$solrURL()}main/select/?q=objectType:person&wt=json&rows=99999`)
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

<style scoped>
.link-list a {
  margin-right: 1em;
  padding-left: 1em;
  border-left: 1px solid rgba(0, 0, 0, 0.125);;
}

.link-list a:first-child {
  padding-left: 0px;
  border-left: none;
}

</style>