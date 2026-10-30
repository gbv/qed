<template>
  <span>
      {{ translatedValue }}
  </span>
</template>

<script setup lang="ts">

import {XMLApi} from "~/api/XMLApi";
import {findClassificationCategoryPath, getClassificationLabel} from "~/api/Classifications";

const props = defineProps<{
  appUrl: string,
  classId: string,
  categId?: string,
  showAncestors?: boolean
}>()

const i18n = useI18n();

const fallbackTranslation = () => {
  if (props.categId) {
    return i18n.t(`metadata.classification.${props.classId}.${props.categId}`);
  }
  return i18n.t(`metadata.classification.${props.classId}`);
};

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
    return fallbackTranslation();
  }
  const classificationElement = response.data.value.xml;

  if (classificationElement == null) {
    return fallbackTranslation();
  }

  if (!props.categId) {
    return getClassificationLabel(classificationElement, i18n.locale.value) ?? fallbackTranslation();
  }

  const categoryPath = findClassificationCategoryPath(classificationElement, props.categId);
  if (!categoryPath) {
    return fallbackTranslation();
  }

  const labels = categoryPath
    .map(category => getClassificationLabel(category, i18n.locale.value))
    .filter((label): label is string => label != null && label.length > 0);

  if (labels.length === 0) {
    return i18n.t("sosu.metadata.unknownGenre");
  }

  return props.showAncestors ? labels.join(" > ") : labels[labels.length - 1];
});

</script>

<style>

</style>
