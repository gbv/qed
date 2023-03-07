<template>

  <template v-if="$route.params.aboutPath =='' || $route.params.aboutPath">
    <nav class="navbar">
      <ul class="navbar-nav">
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/about/">
            {{ $t('intro') }}

          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/about/annihilation">
            {{ $t('war_of_annihilation') }}

          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/about/historians">
            {{ $t('soviet_historians') }}

          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/about/team">
            {{ $t('team') }}

          </nuxt-link>
        </li>
      </ul>
    </nav>
  </template>

  <template v-else-if="$route.params.searchPath == '' || $route.params.searchPath">
    <nav class="navbar">
      <ul class="navbar-nav">
        <li class="nav-item">
            <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/search/search">
              {{ $t('search') }}
            </nuxt-link>
        </li>
        <li class="nav-item">
            <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/search/guide">
              {{ $t('user_guide') }}
            </nuxt-link>
        </li>
      </ul>
    </nav>
  </template>

  <template v-else>
  </template>

</template>
