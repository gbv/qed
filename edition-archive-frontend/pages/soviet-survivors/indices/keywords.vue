<template>
  <SovietSurvivorsLayout>
    <template #menu>
      <SovietSurvivorsMenu />
    </template>
    <template #content>
      <h3>{{ $t('sosu.subMenu.indices') }}: {{ languageLabel }}</h3>
      <DanteVocabularyIndex
        vocabulary-id="sovsur_keywords"
        icon="bi-tags"
        search-base-path="/soviet-survivors/search/"
        :language="language" />
    </template>
  </SovietSurvivorsLayout>
</template>

<script lang="ts" setup>
const {t} = useI18n();
const route = useRoute();
const language = computed(() => {
  const value = route.query.language;
  return typeof value === 'string' ? value : 'en';
});
const languageLabel = computed(() => {
  const labels: Record<string, string> = {
    en: 'sosu.subMenu.keywordEnglish',
    de: 'sosu.subMenu.keywordGerman',
    ru: 'sosu.subMenu.keywordRussian',
    uk: 'sosu.subMenu.keywordUkrainian',
  };
  return t(labels[language.value] || labels.en);
});
</script>