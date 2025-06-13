<template>

  <template v-if="$route.params.projectPath =='' || $route.params.projectPath">
    <nav class="navbar">
      <ul class="navbar-nav">
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/languages-of-diplomacy/project/">
            {{ $t('lod.subMenu.intro') }}

          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/languages-of-diplomacy/project/team">
            {{ $t('lod.subMenu.team') }}

          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/languages-of-diplomacy/project/partner">
            {{ $t('lod.subMenu.projectPartner') }}

          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/languages-of-diplomacy/project/edition-guidelines">
            {{ $t('lod.subMenu.editionGuidelines') }}

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
