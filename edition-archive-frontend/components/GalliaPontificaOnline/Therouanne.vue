<template>
  <div class="row">
    <div class="col-3">
      <nav class="navbar mt-5">
        <ul class="navbar-nav">
          <li class="nav-item">
            <nuxt-link class="nav-link" active-class="active" href="/gallia_pontifica_online/regesten/therouanne/">
              {{ $t('startPage') }}
            </nuxt-link>
          </li>
          <li class="nav-item">
            <nuxt-link class="nav-link" active-class="active" href="/gallia_pontifica_online/regesten/therouanne/suche/">
              {{ $t('search') }}
            </nuxt-link>
          </li>
          <li class="nav-item">
            <nuxt-link class="nav-link" active-class="active" href="/gallia_pontifica_online/regesten/therouanne/personen/">
              {{ $t('person_index') }}
            </nuxt-link>
          </li>
          <li class="nav-item">
            <nuxt-link class="nav-link" active-class="actve" href="#">
              Handschriftenverzeichnis
            </nuxt-link>
          </li>
          <li class="nav-item">
            <nuxt-link class="nav-link" active-class="active" href="#">
              Quellen- und Literaturverzeichnis
            </nuxt-link>
          </li>
          <li class="nav-item">
            <nuxt-link class="nav-link" active-class="active" href="#">
              Dekretalenverzeichnis
            </nuxt-link>
          </li>
          <li class="nav-item">
            <nuxt-link class="nav-link" active-class="active" href="#">
              Initienverzeichnis
            </nuxt-link>
          </li>
        </ul>
      </nav>
    </div>
    <div class="col-9">
      <slot name="regesten" />
    </div>
  </div>
</template>