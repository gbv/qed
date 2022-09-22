<template>
  <client-only>
    <li class="nav-item dropdown">
      <a id="languageDropdown" aria-expanded="false" class="nav-link dropdown-toggle" data-bs-toggle="dropdown" href="#"
         role="button">
        <LanguageDisplay :lang="$i18n.locale" />
      </a>
      <ul aria-labelledby="languageDropdown" class="dropdown-menu">
        <li><a class="dropdown-item" href="#" v-on:click.prevent="changeLanguage('de')"><LanguageDisplay lang="de" /></a></li>
        <li><a class="dropdown-item" href="#" v-on:click.prevent="changeLanguage('en')"><LanguageDisplay lang="en" /></a></li>
        <li><a class="dropdown-item" href="#" v-on:click.prevent="changeLanguage('fr')"><LanguageDisplay lang="fr" /></a></li>
      </ul>
    </li>
  </client-only>
  <ssr-only>

  </ssr-only>
</template>

<script lang="ts" setup>

import {useI18n} from "vue-i18n";
import LanguageDisplay from "~/components/LanguageDisplay.vue";

const i18n = useI18n();

function changeLanguage(newLanguage: string) {
  i18n.locale.value = newLanguage;
  window.localStorage.setItem('locale', newLanguage)
}

onMounted(() => {
  const locale = window.localStorage.getItem('locale');
  if (locale != null) {
    i18n.locale.value = locale;
  }
})


</script>

<style scoped>

</style>