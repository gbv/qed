<template>
    <template v-if="$props.content!=null">
      <template v-if="typeof $props.content === 'string'">
        {{ $props.content }}
      </template>
      <template v-else-if="$props.content.type==='Text'">{{ $props.content.text }}</template>
      <GalliaPontificaOnlineRegestPerson v-else-if="$props.content.name==='cei:persName'" :person="$props.content"/>
      <GalliaPontificaOnlineRegestPlace v-else-if="$props.content.name==='cei:placeName'" :place="$props.content"/>
      <GalliaPontificaOnlineRegestBibl v-else-if="$props.content.name === 'cei:bibl'" :bibl="$props.content"/>
      <GalliaPontificaOnlineRegestRef v-else-if=" $props.content.name==='cei:ref'" :refE="$props.content"/>

      <!-- cei:head -->
      <h6 v-else-if="$props.content.type==='Element' && $props.content.name==='cei:head'" class="head">
          <GalliaPontificaOnlineRegestMixedContent v-if="$props.content.content.length>0" :contents="$props.content.content"/>
      </h6>

      <!-- cei:author --->
      <span v-else-if="$props.content.type==='Element' && $props.content.name==='cei:author'" :class="getAttribute($props.content, 'rend')?.value==='medieval'? '':'smallcaps'">
         <GalliaPontificaOnlineRegestMixedContent v-if="$props.content.content.length>0" :contents="$props.content.content"/>
      </span>

      <!-- cei:foreign -->
      <i v-else-if="$props.content.type==='Element' && $props.content.name==='cei:foreign' && getAttribute($props.content,'lang')?.value==='lat'" class="fst-italic">
        <GalliaPontificaOnlineRegestMixedContent v-if="$props.content.content.length>0" :contents="$props.content.content"/>
      </i>

      <!-- cei:sup -->
      <sup v-else-if="$props.content.type==='Element' && $props.content.name==='cei:sup'" class="fst-sup">
        <GalliaPontificaOnlineRegestMixedContent v-if="$props.content.content.length>0" :contents="$props.content.content"/>
      </sup>

      <GalliaPontificaOnlineRegestMixedContent v-else-if="$props.content.type==='Element'" :contents="$props.content.content"/>
    </template>
    <GalliaPontificaOnlineRegestMixedContent v-else v-for="mc in $props.contents.filter(c=> c?.type !=='Attribute')" :content="mc"/>
</template>

<script lang="ts" setup>
import {flattenElement, getAttribute} from "@mycore-org/xml-json-api"

const route = useRoute()
const props = defineProps(["contents", "content"])

const {$solrURL, $backendURL} = useNuxtApp();

</script>

<style scoped>

</style>