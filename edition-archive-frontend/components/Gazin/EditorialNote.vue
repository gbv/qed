<template>
  <a v-on:click.prevent="open=!open" href="#note">{{ $tei(props.note).text() }} <i class="bi bi-book"></i></a>
  <div v-if="open" class="popout">
    <a class="close icon-link float-end" href="#hide" v-on:click.prevent="open=!open"><i
      class="bi bi-x-circle"></i></a>
    <TeiElementConvert v-if="refTarget" :tei-element="refTarget" />
  </div>
</template>

<script setup lang="ts">

import {$tei, type TEIElement, TeiQuery} from "~/api/tei";
import TeiElementConvert from "~/components/Gazin/TeiElementConvert.vue";

const props = defineProps<{
  /**
   * A TEI ref that contains an editorial note.
   */
  note: TEIElement
}>();

const open = ref(false);

const refTarget = computed(() => {
  const targetId = props.note.attributes.corresp?.replace("#", "") || "";
  console.log([ "root is", $tei(props.note).root(), "id is " + targetId ]);
  return $tei(props.note).root().id(targetId).get(0);
});



</script>

<style scoped>
.popout {
  display: block;
  background-color: white;
  border: 1px solid #ccc;
  border-radius: 7px;
  padding: 1rem;
  margin: 1rem;
  min-height: 5rem;
}
</style>