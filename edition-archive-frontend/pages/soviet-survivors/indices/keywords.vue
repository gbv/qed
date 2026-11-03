<template>
  <SovietSurvivorsLayout>
    <template #content>
      <TabsCard
        :tabs="tabs"
        :current="language"
        @tab-changed="tabChanged">
        <template #en>
          <DanteVocabularyIndex
            vocabulary-id="sovsur_keywords"
            :show-mappings="false"
            search-base-path="/soviet-survivors/search/"
            entity-link-param="keywordEntityLink"
            language="en" />
        </template>
        <template #de>
          <DanteVocabularyIndex
            vocabulary-id="sovsur_keywords"
            :show-mappings="false"
            search-base-path="/soviet-survivors/search/"
            entity-link-param="keywordEntityLink"
            language="de" />
        </template>
        <template #ru>
          <DanteVocabularyIndex
            vocabulary-id="sovsur_keywords"
            :show-mappings="false"
            search-base-path="/soviet-survivors/search/"
            entity-link-param="keywordEntityLink"
            language="ru" />
        </template>
        <template #uk>
          <DanteVocabularyIndex
            vocabulary-id="sovsur_keywords"
            :show-mappings="false"
            search-base-path="/soviet-survivors/search/"
            entity-link-param="keywordEntityLink"
            language="uk" />
        </template>
      </TabsCard>
    </template>
  </SovietSurvivorsLayout>
</template>

<script lang="ts" setup>
import {sovietSurvivorsKeywordLanguages} from '~/composables/sovietSurvivorsKeywordLanguages';

const {t} = useI18n();
const route = useRoute();
const router = useRouter();

const language = computed(() => {
  const value = route.query.language;
  return sovietSurvivorsKeywordLanguages.some((keywordLanguage) => keywordLanguage.code === value)
    ? value as string
    : 'en';
});
const tabs = computed(() => sovietSurvivorsKeywordLanguages.map((keywordLanguage) => ({
  id: keywordLanguage.code,
  title: t(keywordLanguage.labelKey),
})));

const tabChanged = ({_new}: { _new: { id: string } }) => {
  router.push({
    query: {
      ...route.query,
      language: _new.id,
    },
  });
};
</script>