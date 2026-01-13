<template>
  <a class="editorial-note" v-on:click.prevent="open=!open" href="#note"><i class="bi bi-chat-right-text"></i> {{ $tei(props.note).text() }}</a>
  <div v-if="open" class="popout">
    <a class="close icon-link float-end" href="#hide" v-on:click.prevent="open=!open"><i
      class="bi bi-x-circle"></i></a>
    <TeiElementConvert v-if="refTarget" :tei-element="refTarget" />
  </div>
</template>

<script setup lang="ts">

import {type TEIElement} from "~/api/tei.model";

const {$tei} = useTei();

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

i {
  margin-right: 3px;
}

/* prevents unwanted line breaks */
.bi::before, [class^="bi-"]::before, [class*=" bi-"]::before {
  display: inline;
}

.editorial-note:hover {
  background-color: rgb(225, 225, 225);
}

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