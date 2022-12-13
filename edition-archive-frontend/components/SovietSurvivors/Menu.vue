<template>
  <nav class="navbar">
    <ul class="navbar-nav">
      <li class="nav-item">
        <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/about/intro">
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
      <li class="nav-item">
        <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/about/contact">
          {{ $t('contact') }}

        </nuxt-link>
      </li>
      <li class="nav-item">
        <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/about/guide">
          {{ $t('user_guide') }}
        </nuxt-link>
      </li>
    </ul>
  </nav>
</template>
