<template>
  <template v-if="$route.params.regesten == 'reims1'">
        <nav class="navbar">
          <ul class="navbar-nav">
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontificia-online/regesten/reims1/">
                {{ $t('gpo.subMenu.reims1.home') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontificia-online/regesten/reims1/historische-einfuehrung">
                {{ $t('gpo.subMenu.reims1.intro') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontificia-online/regesten/reims1/regest/1">
                {{ $t('gpo.subMenu.reims1.regests') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontificia-online/regesten/reims1/regest/suche/einfach?searchString=*">
                {{ $t("gpo.subMenu.reims1.search") }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontificia-online/regesten/reims1/regest/incipits">
                {{ $t('gpo.subMenu.reims1.incipits') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontificia-online/regesten/reims1/regest/handschriften">
                {{ $t('gpo.subMenu.reims1.writings') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontificia-online/regesten/reims1/regest/dekretale">
                {{ $t('gpo.subMenu.reims1.decretals') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontificia-online/regesten/reims1/regest/quellen">
                {{ $t('gpo.subMenu.reims1.sources') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontificia-online/regesten/reims1/regest/personen/">
                {{ $t('gpo.subMenu.reims1.persons') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontificia-online/regesten/reims1/regest/orte/">
                {{ $t('gpo.subMenu.reims1.places') }}
              </nuxt-link>
            </li>
          </ul>
        </nav>
  </template>

  <template v-else-if="$route.params.regesten == 'genf1'">
    <nav class="navbar">
      <ul class="navbar-nav">
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/gallia-pontificia-online/regesten/genf1/">
            {{ $t('start_page') }}
          </nuxt-link>
        </li>
      </ul>
    </nav>
  </template>

  <template v-else>
  </template>

</template>
