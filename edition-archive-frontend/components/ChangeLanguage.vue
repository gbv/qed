<template>
  <a v-if="props.lang" href="#changeLang" class="clickable" v-on:click.prevent="changeLanguage(props.lang)"><slot></slot></a>
  <span v-else><slot></slot></span>
</template>

<script setup lang="ts">
import {useI18n} from "vue-i18n";

const props = defineProps(["lang"]);
const i18n = useI18n();

function changeLanguage(newLanguage: string) {
  //i18n.locale.value = newLanguage;
  i18n.setLocale(newLanguage);
  i18n.setLocaleCookie(newLanguage);
}
</script>

<style scoped>
.clickable {
  cursor: pointer;
}
</style>