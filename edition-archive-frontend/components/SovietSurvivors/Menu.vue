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
          <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/about/team">
            {{ $t('sosu.subMenu.team') }}

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
          <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/about/learning-environment">
            {{ $t('sosu.subMenu.learningEnvironment') }}

          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/about/further-literature">
            {{ $t('sosu.subMenu.furtherLiterature') }}

          </nuxt-link>
        </li>
      </ul>
    </nav>
  </template>

  <template v-else-if="$route.params.searchPath == '' || $route.params.searchPath">

  </template>

  <template v-else-if="$route.path.startsWith('/soviet-survivors/indices')">
    <nav class="navbar">
      <ul class="navbar-nav">
        <li class="nav-item">
          <nuxt-link class="nav-link" :class="{active: !($route.query.language) || $route.query.language === 'en'}" href="/soviet-survivors/indices/keywords?language=en">
            {{ $t('sosu.subMenu.keywordEnglish') }}
          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" :class="{active: $route.query.language === 'de'}" href="/soviet-survivors/indices/keywords?language=de">
            {{ $t('sosu.subMenu.keywordGerman') }}
          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" :class="{active: $route.query.language === 'ru'}" href="/soviet-survivors/indices/keywords?language=ru">
            {{ $t('sosu.subMenu.keywordRussian') }}
          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" :class="{active: $route.query.language === 'uk'}" href="/soviet-survivors/indices/keywords?language=uk">
            {{ $t('sosu.subMenu.keywordUkrainian') }}
          </nuxt-link>
        </li>
      </ul>
    </nav>
  </template>

  <template v-else>
  </template>

</template>
