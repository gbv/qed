<template>
  <div>
    <template v-if="lines">
      <div class="transcription">
        <div v-if="lines.head.length>0" class="mb-3">
          <strong>{{ lines.head }}</strong>
        </div>
        <div v-for="(group, gIndex) in lines.groups" :key="'group-' + gIndex" class="mb-4">
          <div v-for="(line, lIndex) in group.lines" :key="'line-' + gIndex + '-' + lIndex" class="transcription-line">
            {{ line }}
          </div>
        </div>
      </div>
    </template>
    <div v-else class="text-center">
      <span class="spinner-border" role="status" aria-hidden="true"></span>
      <span class="visually-hidden">Loading...</span>
    </div>
  </div>
</template>

<script setup lang="ts">

import {byName, findElement, flattenElement, XMLApi} from "~/api/XMLApi";

const props = defineProps<{ teiUrl: string }>();

const xml = useAsyncData(`transcription-${props.teiUrl}`, async () => {
  const response = await fetch(props.teiUrl);
  let text = await response.text();
  let xml = await XMLApi(text);
  return xml;
});

interface SongText {
  head: string;
  groups: Array<{
    lines: string[];
  }>;
}

const lines = computed(()=> {
  if(xml.data.value == null){
    return null;
  }

  const tei = xml.data.value;

  const body = findElement(tei, byName('body'))[0];

  if(body == null){
    return null;
  }

  const div = findElement(body, byName('div'))[0];
  if(div == null){
    return null;
  }

  const songText: SongText = {
    head: '',
    groups: []
  };

  const head = findElement(div, byName('head'))[0];
  if(head != null) {
    songText.head = flattenElement(head) || '';
  }

  const lgElements = findElement(div, byName('lg'));
  for(const lg of lgElements) {
    const lines = findElement(lg, byName('l')).map(l => flattenElement(l) || '');
    songText.groups.push({lines});
  }
  return songText;
});

</script>