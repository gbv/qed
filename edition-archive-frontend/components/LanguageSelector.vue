<template>
  <client-only>

    <div class="qed-lang-menu">
      <template v-for="loopLocale in locales" :key="loopLocale.code">
        <a
          :class="`dropdown-item ${ locale == loopLocale.code ? 'active' : 'not-active' }`"
        href="#"
        v-on:click.prevent="changeLanguage(loopLocale.code)">{{loopLocale.code.toUpperCase()}}</a>
        <span v-if="locales[locales.length-1].code != loopLocale.code">|</span>
      </template>
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

import LanguageDisplay from "~/components/LanguageDisplay.vue";
import type {Locale} from "@intlify/core-base";

const {locale, locales, setLocale, setLocaleCookie,} = useI18n()



function changeLanguage(newLanguage: Locale) {
  //i18n.locale.value = newLanguage;
  setLocale(newLanguage);
  setLocaleCookie(newLanguage);
}




</script>

<style scoped>

</style>