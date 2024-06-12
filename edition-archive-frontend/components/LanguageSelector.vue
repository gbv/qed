<template>
  <client-only>

    <div class="qed-lang-menu">
      <a
        :class="`dropdown-item ${ i18n.locale.value == 'de' ? 'active' : 'not-active' }`"
        href="#"
        v-on:click.prevent="changeLanguage('de')">DE</a>
      <span>|</span>
      <a
        :class="`dropdown-item ${ i18n.locale.value == 'en' ? 'active' : 'not-active' }`"
        href="#"
        v-on:click.prevent="changeLanguage('en')">EN</a>
      <span>|</span>
      <a
        :class="`dropdown-item ${ i18n.locale.value == 'fr' ? 'active' : 'not-active' }`"
        href="#"
        v-on:click.prevent="changeLanguage('fr')">FR</a>
    </div>

    <!--
    <ul class="navbar-nav">
      <li class="nav-item dropdown">
        <a id="languageDropdown" aria-expanded="false" class="nav-link dropdown-toggle" data-bs-toggle="dropdown" href="#"
           role="button">
          {{ i18n.localeProperties.value.name }}
        </a>
        <ul aria-labelledby="languageDropdown" class="dropdown-menu">
          <li><a class="dropdown-item" href="#" v-on:click.prevent="changeLanguage('de')"><LanguageDisplay lang="de" /></a></li>
          <li><a class="dropdown-item" href="#" v-on:click.prevent="changeLanguage('en')"><LanguageDisplay lang="en" /></a></li>
          <li><a class="dropdown-item" href="#" v-on:click.prevent="changeLanguage('fr')"><LanguageDisplay lang="fr" /></a></li>
        </ul>
      </li>
    </ul>
    -->
  </client-only>

</template>

<script lang="ts" setup>

import {useI18n} from "vue-i18n";
import LanguageDisplay from "~/components/LanguageDisplay.vue";

const i18n = useI18n();

function changeLanguage(newLanguage: string) {
  //i18n.locale.value = newLanguage;
  i18n.setLocale(newLanguage);
  i18n.setLocaleCookie(newLanguage);
}




</script>

<style scoped>

</style>