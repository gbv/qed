<template>

  <span :class="`${popoverClass}`">
    <a class="popout-wrapper" href="#" v-on:click.prevent="model.show ? hideHandschrift():showHandschrift()">
      <i class="bi bi-book"></i>
    </a>
     <slot/>
    <div v-if="model.show && model.manuscript"  class="popout text-start">
      <a class="close icon-link float-end" href="#hide" v-on:click.prevent="hideHandschrift()"><i
        class="bi bi-x-circle"></i></a>
      <div class="shelfmark" v-if="model.manuscript['shelfmark']?.length>0">{{ model.manuscript['shelfmark']?.join() }}</div>
      <div class="dateStr" v-if="model.manuscript['date.string']?.length>0">{{ model.manuscript['date.string']?.join() }}</div>
      <div class="link-list">
          <nuxt-link
            :to="`/gallia-pontificia-online/regesten/${route.params.regesten}/regest/suche/handschriften?handschriftenKey=${props.handschriftenKey}`">{{ $t('search.label') }}</nuxt-link>
          <nuxt-link
            :to="`/gallia-pontificia-online/regesten/${route.params.regesten}/regest/handschriften/#${props.handschriftenKey}`">{{ $t('gpo.pages.manuscriptIndex') }}</nuxt-link>

          <a v-for="key in Object.keys(model.manuscript).filter(str=> str.indexOf('url.') !== -1).sort()"
             :href="model.manuscript[key]">{{
              $t('url_type_' + key.substring('url.'.length).toLowerCase())
            }}</a>
      </div>
    </div>
  </span>
</template>
<script lang="ts" setup>

import {escapeSpecialChars} from "~/api/Utils";

const props = defineProps<{
  handschriftenKey: string
}>();


const route = useRoute();
const model = reactive({show: false, manuscript: null as any, loading: false});

const {$solrURL, $backendURL} = useNuxtApp();

const popoverClass = computed(() => {
  return (model.show ? 'opened ' : '') + "popout-wrapper position-relative d-inline";
});

const loadData = async () => {
  const request = await fetch(`${$solrURL()}main/select/?q=objectType:manuscript&wt=json&fq=identifier.key:${escapeSpecialChars(props.handschriftenKey)}&fl=*&rows=1`);
  const json = await request.json();
  if (json.response.numFound === 0) {
    throw 404;
  }
  model.loading = false;
  model.manuscript = json.response.docs[0];
}

const hideHandschrift = () => {
  model.show = false;
};

const showHandschrift = () => {
  if (!model.manuscript && !model.loading) {
    model.loading = true;
    loadData()
  }
  model.show = true;
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


.popout-wrapper {
  margin-right: 3px;
}

i {
  margin-right: 3px;
}

.opened {
  background-color: rgb(225, 225, 225);
}

ol{
  list-style-type: none;
  padding: 0;
}

.shelfmark {
  font-weight: bold;
}



</style>