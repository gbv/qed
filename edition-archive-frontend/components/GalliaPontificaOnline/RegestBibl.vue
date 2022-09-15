<template>
  <span>
    <template v-for="c in $props.bibl.content">
      <span v-if="c.type==='Text'">{{c.text}}</span>
      <span v-if="c.type==='Element' && c.name==='cei:author'">{{ flattenElement(c) }}</span>
      <sup v-if="c.type==='Element' && c.name==='cei:sup'">{{ flattenElement(c) }}</sup>
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
    </template>
  </span>
</template>

<script setup lang="ts">
import {flattenElement, getAttribute} from "@mycore-org/xml-json-api"
const props = defineProps(["bibl"])
</script>

<style scoped>

</style>