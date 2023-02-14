<template>
  <GalliaPontificaOnlineLayout>

    <template #content>
          <h2>{{ $t("dekretale_index") }}</h2>
          <ul class="list-group list-group-flush mt-5" v-if="data">
            <li v-for="id in data.ids" class="list-group-item">
              {{data.idText[id]}} (<nuxt-link v-for="doc in data.idResults[id]"
                         :href="`/gallia-pontifica-online/regesten/${route.params.regesten}/regest/${doc.idno}`"
              >{{ doc.idno }}<template v-if="doc.idno !== data.idResults[id][ data.idResults[id].length - 1].idno">, </template></nuxt-link>)
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
  const {data, error} = await useAsyncData(`objectType:regest,dekretale.key`, async () => {
    const request = await fetch(`${$solrURL()}main/select/?q=objectType:regest&wt=json&rows=99999&fq=dekretale.key:*`)
    const json = await request.json();


    const ids = [];
    const idResults: Record<string, Array<any>> = {};
    const idText: Record<string, string> = {};

    for(let doc of json.response.docs){
      const result = doc;
      for(const keytext of result["dekretale.keytext"]){
        const [key, text] = keytext.split("||");
        idResults[key] = idResults[key] || [];
        if(idResults[key].indexOf(result) === -1){
          idResults[key].push(result);
        }

        idText[key] = idText[key] || text;
        if(ids.indexOf(key) === -1){
          ids.push(key);
        }
      }
    }

    ids.sort((a, b) => idText[a].localeCompare(idText[b]));

    return { ids, idResults, idText };
  });
</script>

<style scoped></style>