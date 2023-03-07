<template>
  <span class="popout-wrapper position-relative d-inline">
    <slot/>
    <a href="#" v-if="personLinkEnabled" v-on:click.prevent="model.show ? hidePerson():loadPerson($props.personId)">
      <i class="bi bi-person"></i>
    </a>
    <div v-if="model.show" class="popout text-start">
      <a class="close icon-link float-end" href="#hide" v-on:click.prevent="hidePerson()"><i class="bi bi-x-circle"></i></a>
      <span v-if="(!('skipDisplayName' in $props) || $props.skipDisplayName === false) && model.loaded && model?.person?.displayName" class="person-title">
        {{ model.person.displayName }}
      </span>
      <div v-if="model.loaded && model.person" class="link-list">
          <a v-for="viaf in model.person?.['identifier.VIAF']" :href="'https://viaf.org/viaf/' + viaf">VIAF</a>
          <a v-for="gnd in model.person?.['identifier.GND']" :href="'https://d-nb.info/gnd/' + gnd">GND</a>
          <a v-for="idref in model.person?.['identifier.IDREF']" :href="'https://www.idref.fr/' + idref">IDREF</a>
          <nuxt-link :to="`/gallia-pontifica-online/regesten/${route.params.regesten}/regest/suche/person?personObj=${model.person.id}`">{{$t('search')}}</nuxt-link>
          <nuxt-link :to="`/gallia-pontifica-online/regesten/${route.params.regesten}/regest/personen/#${model.person.id}`">{{$t('regest_person_index')}}</nuxt-link>
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
  personId?: string,
  skipDisplayName?: boolean,
}>();
const {$solrURL, $backendURL} = useNuxtApp();
const model : { loaded: boolean, show: boolean, person?: {
  id: string
  "displayName": string,
  "identifier.key"?: string[],
  "identifier.VIAF"?: string[],
  "identifier.GND"?: string[],
  "identifier.IDREF"?: string[],
  }} = reactive({loaded: false, show: false });
const personLinkEnabled = computed(() => props.personId !== undefined);
const route = useRoute();



const hidePerson = () => {
  model.show = false;
};
const loadPerson = async (key?: string) => {
  if (key === undefined) {
    return;
  }
  model.show = true;
  if (!model.loaded) {
    const personSearchResp = await fetch(`${$solrURL()}main/select/?q=id:${key}%20AND%20objectType:person&wt=json`);
    model.person = (await personSearchResp.json()).response.docs[0];
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

.person-title{
  font-weight: bold;
}


</style>