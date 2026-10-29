<template>
  <SovietSurvivorsLayout>
    <template #menu>
      <SovietSurvivorsMenu />
    </template>
    <template #content>
      <h3>{{ languageLabel }}</h3>
      <DanteVocabularyIndex
        vocabulary-id="sovsur_keywords"
        :show-mappings="false"
        search-base-path="/soviet-survivors/search/"
        entity-link-param="keywordEntityLink"
        :language="language" />
    </template>
  </SovietSurvivorsLayout>
</template>

<script lang="ts" setup>
import {sovietSurvivorsKeywordLanguages} from '~/composables/sovietSurvivorsKeywordLanguages';

const {t} = useI18n();
const route = useRoute();
const language = computed(() => {
  const value = route.query.language;
  return typeof value === 'string' ? value : 'en';
});
const languageLabel = computed(() => {
  const selectedLanguage = sovietSurvivorsKeywordLanguages.find((keywordLanguage) => keywordLanguage.code === language.value);
  return t(selectedLanguage?.labelKey || sovietSurvivorsKeywordLanguages[0].labelKey);
});
</script>