<template>

  <template v-if="$route.params.editionGuidelinesPath =='' || $route.params.editionGuidelinesPath">
    <nav class="navbar">
      <ul class="navbar-nav">
        <li class="nav-item">
          <nuxt-link
            class="nav-link"
            active-class="active"
            href="/gazin/guidelines/transcription">
            {{ $t('gazin.subMenu.transcription') }}
          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link
            class="nav-link"
            active-class="active"
            href="/gazin/guidelines/translation">
            {{ $t('gazin.subMenu.translation') }}
        </nuxt-link>
        </li>
      </ul>
    </nav>
  </template>

    <template v-if="$route.params.editionPath =='' || $route.params.editionPath">
    <nav class="navbar">
      <ul class="navbar-nav">
        <li class="nav-item">
          <nuxt-link
            class="nav-link"
            active-class="active"
            href="/gazin/edition/introduction">
            {{ $t('gazin.subMenu.introduction') }}
          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link
            class="nav-link"
            active-class="active"
            href="/gazin/edition/dengbej-gazin">
            {{ $t('gazin.subMenu.dengbejGazin') }}
          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link
            class="nav-link"
            active-class="active"
            href="/gazin/edition/export">
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