<template>

  <template v-if="$route.params.projectPath =='' || $route.params.projectPath">
    <nav class="navbar">
      <ul class="navbar-nav">
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/languages-of-diplomacy/project">
            {{ $t('lod.subMenu.intro') }}
          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/languages-of-diplomacy/project/team">
            {{ $t('lod.subMenu.team') }}
          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/languages-of-diplomacy/project/acknowledgments">
            {{ $t('lod.subMenu.acknowledgments') }}
          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/languages-of-diplomacy/project/publications">
            {{ $t('lod.subMenu.publications') }}
          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/languages-of-diplomacy/project/export">
            {{ $t('lod.subMenu.export') }}
          </nuxt-link>
        </li>
      </ul>
    </nav>
  </template>

  <template v-if="$route.params.guidelinesPath =='' || $route.params.guidelinesPath">
    <nav class="navbar">
      <ul class="navbar-nav">
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/languages-of-diplomacy/guidelines">
            {{ $t('lod.subMenu.types') }}
          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/languages-of-diplomacy/guidelines/descriptions">
            {{ $t('lod.subMenu.descriptions') }}
          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/languages-of-diplomacy/guidelines/metadata">
            {{ $t('lod.subMenu.metadata') }}
          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/languages-of-diplomacy/guidelines/titles">
            {{ $t('lod.subMenu.titles') }}
          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/languages-of-diplomacy/guidelines/names">
            {{ $t('lod.subMenu.names') }}
          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/languages-of-diplomacy/guidelines/dates">
            {{ $t('lod.subMenu.dates') }}
          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/languages-of-diplomacy/guidelines/principles">
            {{ $t('lod.subMenu.principles') }}
          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/languages-of-diplomacy/guidelines/copies">
            {{ $t('lod.subMenu.copies') }}
          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/languages-of-diplomacy/guidelines/marking">
            {{ $t('lod.subMenu.marking') }}
          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/languages-of-diplomacy/guidelines/commentary">
            {{ $t('lod.subMenu.commentary') }}
          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/languages-of-diplomacy/guidelines/references">
            {{ $t('lod.subMenu.references') }}
          </nuxt-link>
        </li>
        <li class="nav-item">
          <nuxt-link class="nav-link" active-class="active" href="/languages-of-diplomacy/guidelines/tei">
            {{ $t('lod.subMenu.tei') }}
          </nuxt-link>
        </li>
      </ul>
    </nav>
  </template>

  <template v-else-if="$route.params.searchPath == '' || $route.params.searchPath">

  </template>

  <template v-else>
  </template>

</template>
