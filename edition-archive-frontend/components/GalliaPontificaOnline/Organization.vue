<template>
  <span class="popout-wrapper position-relative d-inline">
    <a href="#" v-if="oragnizationLinkEnabled" v-on:click.prevent="model.show ? hideOrganization():loadOrganization($props.organizationId)">
      <i class="bi bi-geo-alt"></i>
    </a>
    <slot/>
    <div v-if="model.show" class="popout text-start">
      <a class="close icon-link float-end" href="#hide" v-on:click.prevent="hideOrganization()"><i class="bi bi-x-circle"></i></a>
      <span v-if="(!('skipDisplayName' in $props) || $props.skipDisplayName === false) && model.loaded && model?.organization?.displayName" class="person-title">
        {{ model.organization.displayName }}
      </span>
      <div v-if="model.loaded && model.organization" class="link-list">
          <a v-for="viaf in model.organization?.['identifier.VIAF']" :href="'https://viaf.org/viaf/' + viaf">VIAF</a>
          <a v-for="gnd in model.organization?.['identifier.GND']" :href="'https://d-nb.info/gnd/' + gnd">GND</a>
          <a v-for="idref in model.organization?.['identifier.IDREF']" :href="'https://www.idref.fr/' + idref">IDREF</a>
          <nuxt-link :to="`/gallia-pontifica-online/regesten/${route.params.regesten}/regest/suche/organisation?organisationObj=${model.organization.id}`">{{$t('search.label')}}</nuxt-link>
          <nuxt-link :to="`/gallia-pontifica-online/regesten/${route.params.regesten}/regest/orte/#${model.organization.id}`">{{$t('gpo.pages.placeIndex')}}</nuxt-link>
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

import {escapeSpecialChars} from "~/api/Utils";

const props = defineProps<{
  organizationId?: string,
  skipDisplayName?: boolean,
}>();
const {$solrURL, $backendURL} = useNuxtApp();
const model : { loaded: boolean, show: boolean, organization?: {
  id: string
  "displayName": string,
  "identifier.key"?: string[],
  "identifier.VIAF"?: string[],
  "identifier.GND"?: string[],
  "identifier.IDREF"?: string[],
  }} = reactive({loaded: false, show: false });
const oragnizationLinkEnabled = computed(() => props.organizationId !== undefined);
const route = useRoute();



const hideOrganization = () => {
  model.show = false;
};
const loadOrganization = async (key?: string) => {
  if (key === undefined) {
    return;
  }
  model.show = true;
  if (!model.loaded) {
    const organizationSearchResp = await fetch(`${$solrURL()}main/select/?q=id:${escapeSpecialChars(key)}%20AND%20objectType:organization&wt=json`);
    model.organization = (await organizationSearchResp.json()).response.docs[0];
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