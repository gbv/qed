<template>
  {{ $t(props.messageKey, translationParameters)}}
</template>

<script setup lang="ts">

import {and, byAttr, byName, findFirstElement, getAttribute, XMLApi} from "~/api/XMLApi";

const i18n = useI18n();

const props = defineProps<{
  classifications: Array<{
    key: string;
    appUrl: string;
    classId: string;
    categId: string;
  }>,
  messageKey: string;
  otherProps: any;
}>();


const response = useAsyncData(`clazz-${
  props.classifications
    .map((clazz) => clazz.appUrl + "-" + clazz.classId)
    .join("-")}`, async () => {

  const proms = Promise.all(
    props.classifications
      .map((clazz) => {
      return fetch(`${clazz.appUrl}api/v2/classifications/${clazz.classId}`)
        .then(r => {
          return r.text();
        }).then(async text => {
          return {appUrl: clazz.appUrl, class: clazz.classId, xml: await XMLApi(text) };
        })
    })
  )
  return await proms;
});

const translatedClasses = computed(() => {
  if (response.data.value == null) {
    return [];
  }

  return props.classifications.map((clazz) => {
    const {
      classId,
      categId,
      appUrl,
      key
    } = clazz;

    const classificationElement = response.data
      .value
      ?.find((r) => r.class == classId && r.appUrl == appUrl)
      ?.xml;

    if(!classificationElement) {
      return undefined;
    }

    const categoryElement = findFirstElement(classificationElement, and(byName("category"),
      byAttr("ID", clazz.categId)));


    if(!categoryElement) {
      return undefined;
    }

    let labelElement = findFirstElement(categoryElement, and(byName("label"),
      byAttr("xml:lang", i18n.locale.value)));


    if (labelElement == null) {
      labelElement = findFirstElement(categoryElement, byName("label"));
    }

    if(labelElement == null) {
      return undefined;
    }

    const label = getAttribute(labelElement, "text")?.value;
    return { key, label };
  });
});


const translationParameters = computed(() => {
  const params: Record<string, any> = {};
  translatedClasses.value.forEach((translatedClass) => {
    if(translatedClass) {
      params[translatedClass.key] = translatedClass.label;
    }
  });


  for(const key in props.otherProps) {
    if(props.otherProps.hasOwnProperty(key)) {
      params[key] = props.otherProps[key];
    }
  }

  return params;
});

</script>

<style scoped>

</style>