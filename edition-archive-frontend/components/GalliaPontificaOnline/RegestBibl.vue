<template>
  <span :class="showSource.show ? 'opened':''" class="bibl">
    <template v-for="c in $props.bibl.content">
      <span v-if="c.type==='Element' && c.name==='cei:ref'">
        <a v-if="c.content.filter(cc => cc.type==='Attribute' && cc.name==='type' && cc.value==='external').length>0"
           :href="getAttribute(c, 'target').value"
        >
          {{ flattenElement(c) }}
        </a>
        <span class="ref" v-else>
          {{ flattenElement(c) }}
        </span>
      </span>
      <GalliaPontificaOnlineRegestMixedContent v-else :content="c"/>
    </template>

    <div v-if="getAttribute($props.bibl,'key') !== null" class="popout-wrapper position-relative d-inline">
      <a :href="'#' + getAttribute($props.bibl,'key').value" v-on:click.prevent="showSource.show ? hideSource():loadSource(getAttribute($props.bibl,'key').value)"><i class="bi bi-book"></i></a>
        <div v-if="showSource.show" class="popout" v-on:blur="hideSource()">
          <a class="close" href="#hide" v-on:click.prevent="hideSource()"><i class="bi bi-x-circle"></i></a>
          <template v-if="showSource.loaded">
            <GalliaPontificaOnlineSource :source="showSource.source"/>
          </template>
          <template v-else="">
            LOADING
          </template>
        </div>
    </div>
  </span>
</template>

<script setup lang="ts">
import {flattenElement, getAttribute} from "@mycore-org/xml-json-api"

const props = defineProps(["bibl"])

const {$solrURL, $backendURL} = useNuxtApp();

const showSource = reactive({loaded: false, show: false, source: {} as any});

const hideSource = () => {
  showSource.show = false;
};

const loadSource = async (key: string) => {
  showSource.show = true;
  if (!showSource.loaded) {
    const sourceSearchResp = await fetch(`${$solrURL()}main/select/?q=identifier.key:${key}%20AND%20objectType:source&wt=json`);
    showSource.source = (await sourceSearchResp.json()).response.docs[0];
    showSource.loaded = true;
  }
};

</script>

<style scoped>

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
  position: absolute;
  z-index: 99;
  display: block;
  background-color: white;
  border: 1px solid #ccc;
  border-radius: 7px 0px 7px 7px;

  padding: 1em;
  top: 20px;
  right: 20px;
  min-height: 100px;
  max-height: 300px;
  width: 300px;

}
</style>