<template>
  <div class="body-box">

    <header class="project-header">
      <div class="project-header__parent">
        <a href="https://perspectivia.net">
          <nuxt-img src="/images/perspectivia_logo_p.png" preload class="" alt="Logo Max-Weber-Stiftung" />
        </a>
      </div>
      <div class="project-header__project">
        <h1>
          <a href="/" title="Das Quellen- und Editionsportal der Max Weber Stiftung">
            qed.perspectivia.net
          </a>
        </h1>
        <div class="beta-batch">
          <span class="badge bg-danger">beta</span>
        </div>
      </div>
      <div class="container">
        <div class="row">
          <div class="col project-header__menu">
            <nav class="navbar navbar-dark navbar-expand-md w-100 justify-content-end">
              <button
                class="navbar-toggler"
                type="button"
                data-bs-toggle="collapse"
                data-bs-target="#qed-main-menu"
                aria-controls="qed-main-menu"
                aria-expanded="false"
                aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
              </button>
              <div class="collapse navbar-collapse" id="qed-main-menu">
                <ul class="navbar-nav w-100 justify-content-end">
                  <li class="nav-item">
                    <nuxt-link class="nav-link js-close-gpo" active-class="active" href="/">{{ $t('qed.mainMenu.home') }}</nuxt-link>
                  </li>
                  <li class="nav-item">
                    <nuxt-link class="nav-link" active-class="active" href="/suche">{{ $t('qed.mainMenu.search') }}</nuxt-link>
                  </li>
                  <li class="nav-item">
                    <nuxt-link class="nav-link" active-class="active" href="/ueber">{{ $t('qed.mainMenu.about') }}</nuxt-link>
                  </li>
                  <!--
                    <LoginMenuComponent />
                  -->
                  <LanguageSelector />
                </ul>
              </div>
            </nav>

          </div>
        </div>
      </div>
    </header>

    <section class="project-content content-intro">
      <div class="container">
        <slot/>
      </div>
    </section>

    <footer class="project-footer">
      <section class="project-footer__nav">
        <div class="container">
          <div class="row">
            <div class="col-auto  project-footer__links">
              <a class="mws-link" href="https://www.maxweberstiftung.de">Max Weber Stiftung</a>
              <a class="p-link" href="https://perspectivia.net">perspectivia.net</a>
            </div>
            <div class="col  project-footer__menu">
              <ul class="nav flex-column">
                <li class="nav-item">
                  <nuxt-link class="nav-link" active-class="active" href="/impressum">Impressum</nuxt-link>
                </li>
                <li class="nav-item">
                  <nuxt-link class="nav-link" active-class="active" href="/datenschutz">Datenschutz</nuxt-link>
                </li>
                <li class="nav-item">
                  <nuxt-link class="nav-link" active-class="active" href="/nutzungsbedingungen">Nutzungsbedingungen</nuxt-link>
                </li>
                <li class="nav-item">
                  <nuxt-link class="nav-link" active-class="active" href="/barrierefreiheit">Barrierefreiheit</nuxt-link>
                </li>
              </ul>
            </div>
            <div class="col-auto project-footer__logo">
              <nuxt-img
                preload
                src="/images/mws_logo_standard_de_weiss.png"
                alt="Logo Max Weber Stiftung" />
            </div>
          </div>
        </div>
      </section>
      <section class="project-footer__credits">
        <div class="container">
          <div class="row">
            <div class="col">
              <div class="project-powered-by text-center">
                <a href="http://www.mycore.de/">
                  <nuxt-img alt="powered by MyCoRe" preload title="todo:add version number"
                       src="/images/mycore_logo_small_invert.png" />
                </a>
              </div>
            </div>
          </div>
        </div>
      </section>
    </footer>

  </div>
</template>

<script setup>
  const i18n = useI18n();
  useHead ({
    htmlAttrs: {
      lang: i18n.locale
    }
  })
</script>
