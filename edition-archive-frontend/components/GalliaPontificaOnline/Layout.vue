<template>
<div>
  <div class="row">
    <div class="col">
      <div class="gpo-headline">
        <h1>Gallia Pontificia online</h1>
        <a href="https://www.dhi-paris.fr/home.html">
          <img src="/images/dhi-paris-logo-460.png" alt="DHI Paris Logo" />
        </a>
      </div>
    </div>
  </div>

  <div class="row">
    <div class="col gpo-main-menu">
      <nav class="navbar navbar-expand-lg">

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
          <ul class="navbar-nav">
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" aria-current="page" href="/gallia-pontifica-online/">
                {{ $t('project') }}
              </nuxt-link>
            </li>
            <li class="nav-item dropdown">
              <nuxt-link class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                {{ $t('regest') }}
              </nuxt-link>
              <ul class="dropdown-menu">
                <li><nuxt-link class="dropdown-item" active-class="active" href="/gallia-pontifica-online/regesten/reims1/">{{ $t('reims1') }}</nuxt-link></li>
                <li><nuxt-link class="dropdown-item" active-class="active" href="/gallia-pontifica-online/regesten/genf1/">{{ $t('genf1') }}</nuxt-link></li>
              </ul>
            </li>
            <li class="nav-item dropdown">
              <nuxt-link class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                {{ $t('editions') }}
              </nuxt-link>
              <ul class="dropdown-menu">
                <li><nuxt-link class="dropdown-item" active-class="active" href="/gallia-pontifica-online/editions/paris">{{ $t('paris') }}</nuxt-link></li>
              </ul>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontifica-online/publikationen">{{ $t('publications') }}</nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontifica-online/zitieren">{{ $t('cite') }}</nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/gallia-pontifica-online/kontakt">{{ $t('contact') }}</nuxt-link>
            </li>
          </ul>
        </div>

      </nav>
    </div>
  </div>

  <div class="row" v-if="$route.params.regesten == 'reims1' || $route.params.regesten == 'genf1'">
    <div class="col-12 gpo-sub-headline">
      <h2>
        <span v-if="$route.params.regesten == 'reims1'">{{ $t('reims1') }}</span>
        <span v-else-if="$route.params.regesten == 'genf1'">{{ $t('genf1') }}</span>
        <span v-else>Subpage</span>
      </h2>
    </div>
    <div class="col-3 gpo-sub-menu">
      <slot name="menu" />
    </div>
    <div class="col-9 gpo-content">
      <slot name="content" />
    </div>
  </div>

  <div v-else>
    <slot name="content" />
  </div>
</div>
</template>
