<template>

  <template v-if="$route.params.aboutPath || $route.params.aboutPath == ''">
    <nav class="navbar">
      <ul class="navbar-nav">
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/about/annihilation">
            Germany’s War of Annihilation
          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/about/historians">
            The Soviet Historians
          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/about/team">
            Team
          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/about/contact">
            Contact
          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/about/guide">
            User Guide
          </nuxt-link>
        </li>
      </ul>
    </nav>
  </template>

  <template v-else>
  </template>

</template>
