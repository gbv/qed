<template>
  <template v-if="$route.params.regesten == 'reims1'">
        <nav class="navbar">
          <ul class="navbar-nav">
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontifica-online/regesten/reims1/">
                {{ $t('start_page') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontifica-online/regesten/reims1/historische-einfuehrung">
                {{ $t('intro') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontifica-online/regesten/reims1/regest/1">
                {{ $t('regests') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontifica-online/regesten/reims1/regest/suche/einfach?searchString=*">
                {{ $t('search') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontifica-online/regesten/reims1/incipits">
                {{ $t('incipits') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontifica-online/regesten/reims1/handschriften">
                {{ $t('writings') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontifica-online/regesten/reims1/dekretalen">
                {{ $t('decretals') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontifica-online/regesten/reims1/regest/quellen">
                {{ $t('sources') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontifica-online/regesten/reims1/regest/personen/">
                {{ $t('person_index') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontifica-online/regesten/reims1/regest/orte/">
                {{ $t('place_index') }}
              </nuxt-link>
            </li>
          </ul>
        </nav>
  </template>

  <template v-else-if="$route.params.regesten == 'genf1'">
    <nav class="navbar">
      <ul class="navbar-nav">
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/gallia-pontifica-online/regesten/genf1/">
            {{ $t('start_page') }}
          </nuxt-link>
        </li>
      </ul>
    </nav>
  </template>

  <template v-else>
  </template>

</template>
