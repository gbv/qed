<template>
  <GalliaPontificaOnlineLayout>

    <template #content>
      <h2>{{ $t("gpo.pages.dekretaleIndex") }}</h2>
      <ul v-if="data" class="list-group list-group-flush mt-5">
        <li v-for="id in data.ids" :id="id" class="list-group-item" :class="highlight===id? 'text-secondary' : ''">
          {{ data.idText[id] }} (
          <nuxt-link v-for="doc in data.idResults[id]"
                     :href="`/gallia-pontifica-online/regesten/${route.params.regesten}/regest/${doc.idno}`"
          >{{ doc.idno }}
            <template v-if="doc.idno !== data.idResults[id][ data.idResults[id].length - 1].idno">,</template>
          </nuxt-link>
          )
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
const {data, error} = await useAsyncData(`objectType:regest,dekretale.key`, async () => {
  const request = await fetch(`${$solrURL()}main/select/?q=objectType:regest&wt=json&rows=99999&fq=dekretale.key:*`)
  const json = await request.json();


  const ids = [];
  const idResults: Record<string, Array<any>> = {};
  const idText: Record<string, string> = {};

  for (let doc of json.response.docs) {
    const result = doc;
    for (const keytext of result["dekretale.keytext"]) {
      const [key, text] = keytext.split("||");
      idResults[key] = idResults[key] || [];
      if (idResults[key].indexOf(result) === -1) {
        idResults[key].push(result);
      }

      idText[key] = idText[key] || text;
      if (ids.indexOf(key) === -1) {
        ids.push(key);
      }
    }
  }

  ids.sort((a, b) => idText[a].localeCompare(idText[b]));


  return {ids, idResults, idText};
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

<style scoped></style>