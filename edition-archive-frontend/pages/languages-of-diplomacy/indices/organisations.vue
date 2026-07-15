<template>
  <LoDLayout>
    <template #content>
      <h3>{{ $t("register.organisation") }}</h3>
      <DanteVocabularyIndex vocabulary-id="lod_organisations" icon="bi-bank"/>
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
