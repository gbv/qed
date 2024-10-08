<template>

  <div :class="$route.params.gpoPath == '' ? 'project-headline project-headline--home':'project-headline'">
    <div class="container">
      <div class="row ">
        <div id="gpo-headline" class="col project-headline__title">
          <h1>Gallia Pontificia online</h1>
        </div>
        <div class="col project-headline__figure">
          <nuxt-img src="/images/card-back-lettre-pape.png" preload class="" alt="Logo GPO" />
        </div>
      </div>
      <div class="project-headline__back project-headline__back--start">
        <nuxt-img src="/images/PatternStrichpunktGrossKreisViertel.svg" preload class="" alt="xxx" />
      </div>
      <div class="project-headline__back project-headline__back--end d-none d-lg-block">
        <nuxt-img src="/images/PatternStrichpunktGrossKreisViertel.svg" preload class="" alt="xxx" />
      </div>
    </div>
  </div>

  <div class="container">

    <div class="row">
      <div class="col project-main-menu">
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
            <ul class="nav nav-pills qed-sub-menu">
              <li class="nav-item">
                <nuxt-link
                  class="nav-link"
                  active-class="active"
                  aria-current="page"
                  href="/gallia-pontificia-online/#gpo-headline">
                  {{ $t('gpo.mainMenu.project') }}
                </nuxt-link>
              </li>
              <li class="nav-item dropdown">
                <nuxt-link
                  class="nav-link dropdown-toggle"
                  active-class="active"
                  href="/gallia-pontificia-online/regesten/reims1/#gpo-headline"
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
                      href="/gallia-pontificia-online/regesten/reims1/#gpo-headline">
                      {{ $t('gpo.mainMenu.regesta.reims1') }}
                    </nuxt-link>
                  </li>
                </ul>
              </li>
              <li class="nav-item">
                <nuxt-link
                  class="nav-link"
                  active-class="active"
                  href="/gallia-pontificia-online/publikationen#gpo-headline">
                  {{ $t('gpo.mainMenu.publications') }}
                </nuxt-link>
              </li>
              <li class="nav-item">
                <nuxt-link
                  class="nav-link"
                  active-class="active"
                  href="/gallia-pontificia-online/zitieren#gpo-headline">
                  {{ $t('gpo.mainMenu.citation') }}
                </nuxt-link>
              </li>
              <li class="nav-item">
                <nuxt-link
                  class="nav-link"
                  active-class="active"
                  href="/gallia-pontificia-online/kontakt#gpo-headline">
                  {{ $t('gpo.mainMenu.contact') }}
                </nuxt-link>
              </li>
            </ul>
          </div>
        </nav>
      </div>

      <div class="col-auto">
        <div class="project-institute">
          <a href="https://www.dhi-paris.fr/home.html">
            <nuxt-img
              src="/images/dhi-paris-logo-460.png"
              preload alt="DHI Paris Logo" />
          </a>
        </div>
      </div>

    </div>

    <div
      class="row"
      v-if="$route.params.regesten == 'reims1' || $route.params.regesten == 'genf1'">
      <div class="col-12 col-xl-3 project-sub-menu">
        <!--slot name="menu" /-->

        <button
          class="btn btn-light d-xl-none side-menu-button"
          type="button"
          data-bs-toggle="offcanvas"
          data-bs-target="#offcanvasExample"
          aria-controls="offcanvasExample">
          {{ $t('gpo.subMenu.openSubMenu') }}
          <i class="bi bi-chevron-right"></i>
        </button>

        <div
          class="offcanvas-xl offcanvas-start"
          data-bs-scroll="true"
          data-bs-backdrop="true"
          tabindex="-1"
          id="offcanvasExample"
          aria-labelledby="offcanvasExampleLabel">
          <div class="offcanvas-header">
            <h5 class="offcanvas-title" id="offcanvasExampleLabel">
              {{ $t('gpo.subMenu.subMenu') }}
            </h5>
            <button
              type="button"
              class="btn-close"
              data-bs-target="#offcanvasExample"
              data-bs-dismiss="offcanvas"
              aria-label="Close">
            </button>
          </div>
          <div class="offcanvas-body">
            <slot name="menu" />
          </div>
        </div>

      </div>
      <div class="col-12 col-xl-9 project-content external-links">
        <h2 class="project-sub-headline">
          <span v-if="$route.params.regesten == 'reims1'">
            {{ $t('gpo.pages.regesta.reims1.headline') }}
          </span>
          <span v-else-if="$route.params.regesten == 'genf1'">
            {{ $t('gpo.pages.regesta.genf1.headline') }}
          </span>
          <span v-else>Subpage</span>
        </h2>
        <slot name="content" />
      </div>
    </div>

    <div v-else class=" external-links">
      <slot name="content" />
    </div>
  </div>
</template>
