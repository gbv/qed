<template>
  <div>

    <div class="row">
      <div class="col">
        <div class="gpo-headline">
          <h1>Soviet Survivors</h1>
          <a href="https://www.dhi-paris.fr/home.html">
            <nuxt-img src="/images/dhi-moskau-logo-200.jpg" preload alt="DHI Moskau Logo" />
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
                <nuxt-link class="nav-link" active-class="active" aria-current="page" href="/soviet-survivors/">
                  Home
                </nuxt-link>
              </li>
              <li class="nav-item">
                <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/about/">
                  About
              </nuxt-link>
              </li>
              <li class="nav-item">
                <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/documents">
                  Documents
                </nuxt-link>
              </li>
              <li class="nav-item">
                <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/search">
                  Search
                </nuxt-link>
              </li>
            </ul>
          </div>

        </nav>
      </div>
    </div>

    <div class="row" v-if="$route.params.aboutPath || $route.params.aboutPath == '' ">

      <div class="col-12 gpo-sub-headline">
        <h2>About</h2>
      </div>

      <div class="col-md-3 col-sm-12 gpo-sub-menu">
        <nav class="navbar">
          <ul class="navbar-nav">
            <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/about/annihilation">
                Germany’s War of Annihilation
              </nuxt-link>
              <li class="nav-item">
              <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/about/historians">
                The Soviet Historians
              </nuxt-link>
              <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/about/team">
                Team
              </nuxt-link>
              <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/about/contact">
                Contact
              </nuxt-link>
              <nuxt-link class="nav-link" active-class="active" href="/soviet-survivors/about/guide">
                User Guide
              </nuxt-link>
            </li>
            </li>
          </ul>
        </nav>
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
