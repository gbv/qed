<template>

  <div class="page-headline">
    Kopfbild SoSu
  </div>

  <div class="container">

    <div class="row">
      <div class="col">
        <div class="gpo-headline">
          <h1>Soviet Survivors</h1>
          <a href="https://www.dhi-moskau.org/">
            <nuxt-img src="/images/dhi-moskau-logo.png" preload alt="DHI Moskau Logo" />
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
                <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/">
                  {{ $t('sosu.mainMenu.home') }}
                </nuxt-link>
              </li>
              <li class="nav-item">
                <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/about/">
                  {{ $t('sosu.mainMenu.about') }}
              </nuxt-link>
              </li>
              <li class="nav-item">
                <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/documents/39?search=*&start=0">
                  {{ $t('sosu.mainMenu.documents') }}
                </nuxt-link>
              </li>
              <li class="nav-item">
                <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/search/">
                  {{ $t('sosu.mainMenu.search') }}
                </nuxt-link>
              </li>
              <li class="nav-item">
                <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/contact">
                  {{ $t('sosu.mainMenu.contact') }}
                </nuxt-link>
              </li>
            </ul>
          </div>

        </nav>
      </div>
    </div>

    <div class="row" v-if="'menu' in $slots">

      <div class="col-12 gpo-sub-headline" v-if="'headline' in $props">
        <h2>{{$props.headline}}</h2>
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

<script lang="ts" setup>
  const props = defineProps(["headline"])
</script>
