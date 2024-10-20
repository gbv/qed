<template>
  <template v-if="$route.params.regesten == 'reims1'">
        <nav class="navbar">
          <ul class="navbar-nav">
            <li class="nav-item nav-item-search">
              <nuxt-link
                class="nav-link"
                active-class="active"
                href="/gallia-pontificia-online/regesten/reims1/regest/suche/einfach?searchString=*#gpo-headline">
                <i class="bi bi-search"></i>
                {{ $t("gpo.subMenu.reims1.search") }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontificia-online/regesten/reims1/#gpo-headline">
                {{ $t('gpo.subMenu.reims1.home') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontificia-online/regesten/reims1/historische-einfuehrung#gpo-headline">
                {{ $t('gpo.subMenu.reims1.intro') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontificia-online/regesten/reims1/regest/1#gpo-headline">
                {{ $t('gpo.subMenu.reims1.regests') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontificia-online/regesten/reims1/regest/incipits#gpo-headline">
                {{ $t('gpo.subMenu.reims1.incipits') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontificia-online/regesten/reims1/regest/handschriften#gpo-headline">
                {{ $t('gpo.subMenu.reims1.writings') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontificia-online/regesten/reims1/regest/dekretale#gpo-headline">
                {{ $t('gpo.subMenu.reims1.decretals') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontificia-online/regesten/reims1/regest/quellen#gpo-headline">
                {{ $t('gpo.subMenu.reims1.sources') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontificia-online/regesten/reims1/regest/personen/#gpo-headline">
                {{ $t('gpo.subMenu.reims1.persons') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontificia-online/regesten/reims1/regest/orte/#gpo-headline">
                {{ $t('gpo.subMenu.reims1.places') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontificia-online/regesten/reims1/zitierhinweis#gpo-headline">
                {{ $t('gpo.subMenu.reims1.citation') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontificia-online/regesten/reims1/datenexport#gpo-headline">
                {{ $t('gpo.subMenu.reims1.export') }}
              </nuxt-link>
            </li>
          </ul>
        </nav>
  </template>

  <template v-else-if="$route.params.regesten == 'genf1'">
    <nav class="navbar">
      <ul class="navbar-nav">
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/gallia-pontificia-online/regesten/genf1/#gpo-headline">
            {{ $t('start_page') }}
          </nuxt-link>
        </li>
      </ul>
    </nav>
  </template>

  <template v-else>
  </template>

</template>
