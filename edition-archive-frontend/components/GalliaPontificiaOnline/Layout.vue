<template>
<div>
  <div class="row">
    <div class="col">
      <div class="gpo-headline">
        <h1>Gallia Pontificia online</h1>
        <a href="https://www.dhi-paris.fr/home.html">
          <nuxt-img
            src="/images/dhi-paris-logo-460.png"
            preload alt="DHI Paris Logo" />
        </a>
      </div>
    </div>
  </div>

  <div class="row">
    <div class="col gpo-main-menu">
      <nav class="navbar navbar-expand-lg">

        <button
          class="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarSupportedContent"
          aria-controls="navbarSupportedContent"
          aria-expanded="false"
          aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
          <ul class="navbar-nav">
            <li class="nav-item">
              <nuxt-link
                class="nav-link"
                active-class="active"
                aria-current="page"
                href="/gallia-pontificia-online/">
                {{ $t('gpo.mainMenu.project') }}
              </nuxt-link>
            </li>
            <li class="nav-item dropdown">
              <nuxt-link
                class="nav-link dropdown-toggle"
                active-class="active"
                href="#"
                role="button"
                data-bs-toggle="dropdown"
                aria-expanded="false">
                {{ $t('gpo.mainMenu.regesta.label') }}
              </nuxt-link>
              <ul class="dropdown-menu">
                <li>
                  <nuxt-link
                    class="dropdown-item"
                    active-class="active"
                    href="/gallia-pontificia-online/regesten/reims1/">
                    {{ $t('gpo.mainMenu.regesta.reims1') }}
                  </nuxt-link>
                </li>
              </ul>
            </li>
            <li class="nav-item">
              <nuxt-link
                class="nav-link"
                active-class="active"
                href="/gallia-pontificia-online/publikationen">
                {{ $t('gpo.mainMenu.publications') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link
                class="nav-link"
                active-class="active"
                href="/gallia-pontificia-online/zitieren">
                {{ $t('gpo.mainMenu.citation') }}
              </nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link
                class="nav-link"
                active-class="active"
                href="/gallia-pontificia-online/kontakt">
                {{ $t('gpo.mainMenu.contact') }}
              </nuxt-link>
            </li>
          </ul>
        </div>
      </nav>
    </div>
  </div>

  <div
    class="row"
    v-if="$route.params.regesten == 'reims1' || $route.params.regesten == 'genf1'">
    <div class="col-12 gpo-sub-headline">
      <h2>
        <span v-if="$route.params.regesten == 'reims1'">
          {{ $t('gpo.pages.regesta.reims1.headline') }}
        </span>
        <span v-else-if="$route.params.regesten == 'genf1'">
          {{ $t('gpo.pages.regesta.genf1.headline') }}
        </span>
        <span v-else>Subpage</span>
      </h2>
    </div>
    <div class="col-md-3 col-sm-12 gpo-sub-menu">
      <slot name="menu" />
    </div>
    <div class="col-md-9 col-sm-12 gpo-content external-links">
      <slot name="content" />
    </div>
  </div>

  <div v-else class=" external-links">
    <slot name="content" />
  </div>
</div>
</template>
