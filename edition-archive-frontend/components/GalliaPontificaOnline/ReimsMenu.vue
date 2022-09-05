<template>
      <nav class="navbar">
        <ul class="navbar-nav">
          <li class="nav-item">
            <nuxt-link class="nav-link" active-class="active" href="/gallia-pontifica-online/regesten/reims/">
              {{ $t('startPage') }}
            </nuxt-link>
          </li>
          <li class="nav-item">
            <nuxt-link class="nav-link" active-class="active" href="#">
              Historische Einführung
            </nuxt-link>
          </li>
          <li class="nav-item">
            <nuxt-link class="nav-link" active-class="active" href="#">
              Regesten
            </nuxt-link>
          </li>
          <li class="nav-item">
            <nuxt-link class="nav-link" active-class="active" href="/gallia-pontifica-online/regesten/reims/regest/suche/">
              {{ $t('search') }}
            </nuxt-link>
          </li>
          <li class="nav-item">
            <nuxt-link class="nav-link" active-class="active" href="#">
              Incipits
            </nuxt-link>
          </li>
          <li class="nav-item">
            <nuxt-link class="nav-link" active-class="active" href="#">
              Handschriften
            </nuxt-link>
          </li>
          <li class="nav-item">
            <nuxt-link class="nav-link" active-class="active" href="#">
              Dekretalen
            </nuxt-link>
          </li>
          <li class="nav-item">
            <nuxt-link class="nav-link" active-class="active" href="#">
              Quellen- und Literatur
            </nuxt-link>
          </li>
          <li class="nav-item">
            <nuxt-link class="nav-link" active-class="active" href="/gallia-pontifica-online/regesten/reims/regest/personen/">
              {{ $t('person_index') }}
            </nuxt-link>
          </li>
          <li class="nav-item">
            <nuxt-link class="nav-link" active-class="active" href="#">
              Orte
            </nuxt-link>
          </li>
        </ul>
      </nav>
</template>