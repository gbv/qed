<template>

<div :class="$route.params.lodPath == '' ? 'project-headline project-headline--home':'project-headline'">
    <div class="container">
      <div class="row ">
        <div class="col project-headline__title">
          <h1>Languages of Diplomacy</h1>
        </div>
        <div class="col project-headline__figure project-headline__figure--lod">
          <nuxt-img src="/images/card-back-golovkin-mikhail.jpg" preload class="" alt="Logo Languages of Diplomacy" />
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
                  href="/languages-of-diplomacy/">
                  {{ $t('lod.mainMenu.home') }}
                </nuxt-link>
              </li>
              <li class="nav-item">
                <nuxt-link
                  class="nav-link"
                  active-class="active"
                  href="/languages-of-diplomacy/project">
                  {{ $t('lod.mainMenu.project') }}
              </nuxt-link>
              </li>
              <li class="nav-item">
                <nuxt-link
                  class="nav-link"
                  active-class="active"
                  href="/languages-of-diplomacy/search/">
                  {{ $t('lod.mainMenu.search') }}
                </nuxt-link>
              </li>
              <li class="nav-item">
                <nuxt-link
                  class="nav-link"
                  active-class="active"
                  href="/languages-of-diplomacy/contact">
                  {{ $t('lod.mainMenu.contact') }}
                </nuxt-link>
              </li>
            </ul>
          </div>
        </nav>
      </div>

      <div class="col-auto">
        <div class="project-institute project-institute--lod">
          <a href="https://mwsosteuropa.hypotheses.org/">
            <nuxt-img
              src="/images/mwno-logo-de.svg"
              preload alt="Max Weber Netzwerk Osteuropa Logo" />
          </a>
        </div>
      </div>

    </div>



    <div
      class="row"
      v-if="'menu' in $slots">
      <div class="col-12 col-lg-3 project-sub-menu">

        <button
          class="btn btn-light d-lg-none side-menu-button"
          type="button"
          data-bs-toggle="offcanvas"
          data-bs-target="#offcanvasExample"
          aria-controls="offcanvasExample">
          <i class="bi bi-chevron-right"></i>
          Seitenmenü öffnen
        </button>

        <div
          class="offcanvas-lg offcanvas-start"
          data-bs-scroll="true"
          data-bs-backdrop="true"
          tabindex="-1"
          id="offcanvasExample"
          aria-labelledby="offcanvasExampleLabel">
          <div class="offcanvas-header">
            <h5 class="offcanvas-title" id="offcanvasExampleLabel">Seitenmenü</h5>
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
      <div class="col-12 col-lg-9 project-content external-links">
        <slot name="content" />
      </div>
    </div>

    <div v-else class=" external-links">
      <slot name="content" />
    </div>

  </div>
</template>

<script lang="ts" setup>
  const props = defineProps(["headline"])
</script>
