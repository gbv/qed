<template>
  <a v-if="props.lang" href="#changeLang" class="clickable" v-on:click.prevent="changeLanguage(props.lang)"><slot></slot></a>
  <span v-else><slot></slot></span>
</template>

<script setup lang="ts">
import {useI18n} from "vue-i18n";

const props = defineProps(["lang"]);
const i18n = useI18n();
const {locale, locales, setLocale, setLocaleCookie,} = useI18n()

const availableLocales = computed(() => {
  return locales.value.filter(i => i.code !== locale.value)
})

function changeLanguage(newLanguage: string) {
  //i18n.locale.value = newLanguage;
  availableLocales.value.forEach((llocale) => {
    if (llocale.code === newLanguage) {
      setLocale(llocale.code);
      setLocaleCookie(llocale.code);
    }
  });


}
</script>

<style scoped>
.clickable {
  cursor: pointer;
}
</style>