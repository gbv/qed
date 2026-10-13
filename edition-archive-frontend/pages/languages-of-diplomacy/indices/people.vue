<template>
  <LoDLayout>
    <template #content>
      <h3>{{ $t("register.person") }}</h3>
      <DanteVocabularyIndex vocabulary-id="lod_persons" icon="bi-person" language="zxx"
               search-base-path="/languages-of-diplomacy/search/"
               entity-link-param="personEntityLink"/>
    </template>

    <template #menu>
      <LoDMenu/>
    </template>
  </LoDLayout>
</template>

<script lang="ts" setup>
import {DanteEntityPathsKey} from "~/composables/DanteEntity";

provide(DanteEntityPathsKey, {
  searchBasePath: '/languages-of-diplomacy/search/',
});
</script>
