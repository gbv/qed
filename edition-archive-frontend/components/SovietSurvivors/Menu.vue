<template>

  <template v-if="$route.params.aboutPath =='' || $route.params.aboutPath">
    <nav class="navbar">
      <ul class="navbar-nav">
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/about/">
            {{ $t('sosu.subMenu.intro') }}

          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/about/annihilation">
            {{ $t('sosu.subMenu.warOfAnnihilation') }}

          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/about/historians">
            {{ $t('sosu.subMenu.sovietHistorians') }}

          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/about/team">
            {{ $t('sosu.subMenu.team') }}

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
