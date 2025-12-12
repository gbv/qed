<template>

  <template v-if="$route.params.editionGuidelinesPath =='' || $route.params.editionGuidelinesPath">
    <nav class="navbar">
      <ul class="navbar-nav">
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/gazin/edition-guidelines/transcription-notes">
            {{ $t('gazin.subMenu.transcriptionNotes') }}
          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/gazin/edition-guidelines/translation-notes">
            {{ $t('gazin.subMenu.translationNotes') }}
        </nuxt-link>
        </li>
      </ul>
    </nav>
  </template>

    <template v-if="$route.params.projectPath =='' || $route.params.projectPath">
    <nav class="navbar">
      <ul class="navbar-nav">
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/gazin/project/introduction">
            {{ $t('gazin.subMenu.introduction') }}
          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/gazin/project/citation">
            {{ $t('gazin.subMenu.citation') }}
        </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/gazin/project/export">
            {{ $t('gazin.subMenu.export') }}
        </nuxt-link>
        </li>
      </ul>
    </nav>
  </template>

  <template v-else-if="$route.params.searchPath == '' || $route.params.searchPath">

  </template>

  <template v-else>
  </template>

</template>
<script setup lang="ts">
</script>