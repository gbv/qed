<template>
  <span>

    <a v-if="$props.type === 'external'" :href="$props.refe">
      <slot/>
    </a>

    <span v-if="$props.type === 'person'">
      <GalliaPontificiaOnlinePerson :person-id="$props.refe">
        <slot/>
      </GalliaPontificiaOnlinePerson>
    </span>

    <span v-if="$props.type === 'place'">
      <GalliaPontificiaOnlinePlace :place-id="$props.refe">
        <slot/>
      </GalliaPontificiaOnlinePlace>
    </span>

    <span v-if="$props.type === 'dekretale'" class="popout-wrapper position-relative d-inline">
      <GalliaPontificiaOnlineDekretale :dekretale-key="`${$props.refe}`">
        <slot />
      </GalliaPontificiaOnlineDekretale>
    </span>

    <a v-else-if="$props.type === 'source'" :href="'#' + $props.refe" class="icon-link"
       v-on:click.prevent="showSource.show ? hideSource():loadSource($props.refe)">
      <i class="bi bi-book"></i> <slot/>
    </a>
    <span v-if="showSource.show" class="popout" v-on:blur="hideSource()">
      <a class="close icon-link" href="#hide" v-on:click.prevent="hideSource()"><i class="bi bi-x-circle"></i></a>
      <template v-if="showSource.loaded">
        <GalliaPontificiaOnlineSource :source="showSource.source"/>
      </template>
      <template v-else="">
        LOADING
      </template>
    </span>
  </span>
</template>

<script lang="ts" setup>
import {flattenElement, getAttribute} from "@mycore-org/xml-json-api"
import {escapeSpecialChars} from "~/api/Utils";

const props = defineProps(["refe", "type"])
const route = useRoute()

const {$solrURL, $backendURL} = useNuxtApp();

const showSource = reactive({loaded: false, show: false, source: {} as any});

const hideSource = () => {
  showSource.show = false;
};

const loadSource = async (key?: string) => {
  if (key === undefined) {
    return;
  }
  showSource.show = true;
  if (!showSource.loaded) {
    const sourceSearchResp = await fetch(`${$solrURL()}main/select/?q=identifier.key:${escapeSpecialChars(key)}%20AND%20objectType:source&wt=json`);
    showSource.source = (await sourceSearchResp.json()).response.docs[0];
    showSource.loaded = true;
  }
};

</script>

<style scoped>

/* prevents unwanted line breaks */
.bi::before, [class^="bi-"]::before, [class*=" bi-"]::before {
  display: inline;
}

.bibl:hover {
  background-color: rgb(225, 225, 225);
}


.opened {
  background-color: rgb(225, 225, 225);
}

.popout-wrapper {
  margin-right: 3px;
  margin-left: 3px;

}

i {
  margin-right: 3px;
}

.close {
  float: right;
}

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
</style>