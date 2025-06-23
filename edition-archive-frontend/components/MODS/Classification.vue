<template>
  <span>
      {{ translatedValue }}
  </span>
</template>

<script setup lang="ts">

import {XMLApi} from "~/api/XMLApi";
import {
  and,
  byAttr,
  byName,
  findElement,
  findFirstElement,
  flattenElement,
  getAttribute
} from "~/api/XMLApi";

const props = defineProps<{
  classId: string,
  categId: string
}>()

const i18n = useI18n();

const {$sovietSurviorsURL} = useNuxtApp();
const sovietSurviorsURL = $sovietSurviorsURL();


const response = useAsyncData(`clazz-${props.classId}`, async () => {
  const response = await fetch(`${sovietSurviorsURL}api/v2/classifications/${props.classId}`);
  return await response.text().then((xmlText) => {
    return XMLApi(xmlText);
  });
});

const translatedValue = computed(() => {
  const classificationElement = response.data.value;

  if (classificationElement == null) {
    return i18n.t("sosu.metadata.unknownGenre");
  }

  const categoryElement = findFirstElement(classificationElement, and(byName("category"),
    byAttr("ID", props.categId)));

  if (categoryElement == null) {
    return i18n.t("sosu.metadata.unknownGenre");
  }

  let labelElement = findFirstElement(categoryElement, and(byName("label"),
    byAttr("xml:lang", i18n.locale.value)));


  if (labelElement == null) {
    labelElement = findFirstElement(categoryElement, byName("label"));
  }

  if (labelElement == null) {
    return i18n.t("sosu.metadata.unknownGenre");
  }

  return getAttribute(labelElement, "text")?.value;
});

</script>

<style>

</style>
