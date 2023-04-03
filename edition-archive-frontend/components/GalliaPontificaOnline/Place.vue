<template>
  <span class="popout-wrapper position-relative d-inline">
    <a href="#" v-if="placeLinkEnabled" v-on:click.prevent="model.show ? hidePlace():loadPlace($props.placeId)">
      <i class="bi bi-geo-alt"></i>
    </a>
    <slot/>
    <div v-if="model.show" class="popout text-start">
      <a class="close icon-link float-end" href="#hide" v-on:click.prevent="hidePlace()"><i class="bi bi-x-circle"></i></a>
      <span v-if="(!('skipDisplayName' in $props) || $props.skipDisplayName === false) && model.loaded && model?.place?.displayName" class="place-title">
        {{ model.place.displayName }}
      </span>
      <div v-if="model.loaded && model.place" class="link-list">
          <a v-for="viaf in model.place?.['identifier.VIAF']" :href="'https://viaf.org/viaf/' + viaf">VIAF</a>
          <a v-for="gnd in model.place?.['identifier.GEONAMES']" :href="'https://www.geonames.org/' + gnd">GEONAMES</a>
          <nuxt-link :to="`/gallia-pontifica-online/regesten/${route.params.regesten}/regest/suche/ort?ortObj=${model.place.id}`">{{$t('search.label')}}</nuxt-link>
          <nuxt-link :to="`/gallia-pontifica-online/regesten/${route.params.regesten}/regest/orte/#${model.place.id}`">{{$t('gpo.pages.placeIndex')}}</nuxt-link>
      </div>
      <template v-else>
        <div class="spinner-border" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </template>
    </div>
  </span>
</template>
<script lang="ts" setup>

const props = defineProps<{
  placeId?: string,
  skipDisplayName?: boolean,
}>();
const {$solrURL, $backendURL} = useNuxtApp();
const model : { loaded: boolean, show: boolean, place?: {
    id: string
    "displayName": string,
    "identifier.key"?: string[],
    "identifier.VIAF"?: string[],
    "identifier.GEONAMES"?: string[],
  }} = reactive({loaded: false, show: false });
const placeLinkEnabled = computed(() => props.placeId !== undefined);
const route = useRoute();



const hidePlace = () => {
  model.show = false;
};
const loadPlace = async (key?: string) => {
  if (key === undefined) {
    return;
  }
  model.show = true;
  if (!model.loaded) {
    const personSearchResp = await fetch(`${$solrURL()}main/select/?q=id:${key}%20AND%20objectType:place&wt=json`);
    model.place = (await personSearchResp.json()).response.docs[0];
    model.loaded = true;
  }
};


</script>

<style scoped>

.popout {
  display: block;
  background-color: white;
  border: 1px solid #ccc;
  border-radius: 7px;
  padding: 1rem;
  margin-top: 1rem;
  margin-bottom: 1rem;
  min-height: 5rem;
}

ol{
  list-style-type: none;
  padding: 0;
}

.place-title{
  font-weight: bold;
}



</style>