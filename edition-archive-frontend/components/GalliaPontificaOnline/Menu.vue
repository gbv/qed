<template>
  <nav class="navbar mt-5">
    <ul class="navbar-nav">
      <li class="nav-item">
        <nuxt-link class="nav-link" active-class="active" href="/gallia_pontifica_online/">
          {{ $t('startPage') }}
        </nuxt-link>
      </li>
      <li class="nav-item">
        <nuxt-link class="nav-link" active-class="active" href="/gallia_pontifica_online/erklaerung">
          {{ $t('explain') }}
        </nuxt-link>
      </li>
      <li class="nav-item">
        <nuxt-link class="nav-link" active-class="active" href="/gallia_pontifica_online/regest/suche/">
          {{ $t('search') }}
        </nuxt-link>
      </li>
      <li class="nav-item">
        <nuxt-link class="nav-link" active-class="active" href="/gallia_pontifica_online/personen/">
          {{ $t('person_index') }}
        </nuxt-link>
      </li>
      <li class="nav-item">
        <nuxt-link class="nav-link" active-class="active" href="/gallia_pontifica_online/kontakt">
          {{ $t('contact') }}
        </nuxt-link>
      </li>
    </ul>
  </nav>
</template>