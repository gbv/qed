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
  appUrl: string,
  classId: string,
  categId?: string
}>()

const i18n = useI18n();

const response = useAsyncData(`clazz-${props.appUrl}-${props.classId}`, async () => {
  const response = await fetch(`${props.appUrl}api/v2/classifications/${props.classId}`);
  const xml = await response.text().then((xmlText) => {
    return XMLApi(xmlText);
  });
  return {
    appUrl: props.appUrl,
    class: props.classId,
    xml
  };
});

const translatedValue = computed(() => {
  if(response.data.value == null) {
    if(props.categId) {
      return i18n.t(`metadata.classification.${props.classId}.${props.categId}`);
    } else {
      return i18n.t(`metadata.classification.${props.classId}`);
    }
  }
  const classificationElement = response.data.value.xml;

  if (classificationElement == null) {
    if(props.categId) {
      return i18n.t(`metadata.classification.${props.classId}.${props.categId}`);
    } else {
      return i18n.t(`metadata.classification.${props.classId}`);
    }
  }

  let categoryElement;
  if(props.categId) {
    categoryElement = findFirstElement(classificationElement, and(byName("category"),
      byAttr("ID", props.categId)));
  } else {
    categoryElement = classificationElement;
  }


  if (categoryElement == null) {
    if(props.categId) {
      return i18n.t(`metadata.classification.${props.classId}.${props.categId}`);
    } else {
      return i18n.t(`metadata.classification.${props.classId}`);
    }
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
