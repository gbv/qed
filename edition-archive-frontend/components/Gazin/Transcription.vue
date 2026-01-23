<template>
  <div>
    <template v-if="body">
      <div class="transcription">
        <tei-element-convert :tei-element="body" />
      </div>
    </template>
    <div v-else class="text-center">
      <span class="spinner-border" role="status" aria-hidden="true"></span>
      <span class="visually-hidden">Loading...</span>
    </div>
  </div>
</template>

<script setup lang="ts">

const props = defineProps<{ teiUrl: string }>();

const {$tei} = useTei();


const teiStr = useAsyncData(`transcription-${props.teiUrl}`, async () => {
  const response = await fetch(props.teiUrl);
  return await response.text();
});

const body = computed(() => {
  if (teiStr.data.value == null) {
    return null;
  }
  const tei = $tei(teiStr.data.value)
  return tei.find("body").toArray()[0] || null;
});



</script>

<style>

:root {
  --qed-counter-width: 3.15rem; /* Platz für die Nummernspalte */
}

.tei-element[data-tei-attr-type="song"] {
  counter-reset: line 0;
}

.tei-element[data-tei-name="head"] {
  display: block;
  background-color: #f1f1f1;
  padding-left: var(--qed-counter-width);
}

.tei-element[data-tei-name="lg"] {
  display: block;
  padding-bottom: 2rem;
  padding-top: 2rem;
  border-bottom: 1px solid #ddd;
}

.tei-element[data-tei-name="lg"]:last-of-type {
  border-bottom: none;
}


.tei-element[data-tei-name="l"] {
  counter-increment: line;
  display: block;
  padding-bottom: 0.2rem;
  margin-bottom: 0.2rem;
}

.tei-element[data-tei-name="l"] {
  position: relative;
  padding-left: var(--qed-counter-width);
  counter-increment: line;
  display: block;
  padding-bottom: 0.2rem;
  margin-bottom: 0.2rem;
  box-sizing: border-box;
}

.tei-element[data-tei-name="l"]::before {
  content: counter(line);
  position: absolute;
  left: 0;
  top: 2px;
  bottom: 0;
  width: var(--qed-counter-width);
  display: flex;
  align-items: start;
  justify-content: flex-end;
  padding-right: 2rem;
  height: auto;
  font-size: 0.7rem;
}

[data-tei-attr-rendition="#b"] {
  font-weight: bold;
}

[data-tei-attr-rendition="#i"] {
  font-style: italic;
}

</style>