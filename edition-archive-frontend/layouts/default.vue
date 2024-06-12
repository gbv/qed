<template>
  <div class="body-box">

    <header class="project-header">
      <div class="container">

        <div class="project-header__box project-header__box--logo">

          <div class="project-header__logo">
            <a href="https://perspectivia.net" class="project-header__logo--p">
              <nuxt-img src="/images/p-logo-solo.svg" preload class="" alt="Logo perspectivia" />
            </a>
            <a href="/"  class="project-header__logo--qed">
              <nuxt-img src="/images/qed-logo-solo.svg" preload class="" alt="Logo QED" />
            </a>
            <a href="/"  class="project-header__logo--beta">
              <nuxt-img src="/images/beta.svg" preload class="" alt="Beta" />
            </a>
          </div>

          <div class="project-header__box project-header__box--menu">

            <LanguageSelector />

            <div class="project-header__menu">
              <nav class="navbar navbar-light w-100 justify-content-end">
                <button
                  class="navbar-toggler"
                  type="button"
                  data-bs-toggle="collapse"
                  data-bs-target="#qed-main-menu"
                  aria-controls="qed-main-menu"
                  aria-expanded="false"
                  aria-label="Toggle navigation">
                  <span class="navbar-toggler__label">Menü</span>
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
                    <li><hr class="dropdown-divider"></li>
                    <li class="nav-item">
                      <nuxt-link class="nav-link" active-class="active" href="/ueber">{{ $t('qed.mainMenu.about') }}</nuxt-link>
                    </li>
                  </ul>
                </div>
              </nav>
            </div>
          </div>

        </div>
      </div>
    </header>

    <section class="project-content content-intro">
      <!--div class="container"-->
        <slot/>
      <!--/div-->
    </section>

    <footer class="project-footer">
      <section class="project-footer__nav">
        <div class="container">
          <div class="row">

            <div class="col-6 col-lg-3 project-footer__logo">
              <a href="https://www.maxweberstiftung.de">
                <nuxt-img
                  preload
                  src="/images/max-weber-stiftung-de.svg"
                  alt="Logo Max Weber Stiftung" />
              </a>
            </div>

            <div class="col-6 col-lg-3  project-footer__menu">
              <ul class="nav flex-column">
                <li class="nav-item">
                  <nuxt-link class="nav-link" active-class="active" href="/impressum">Impressum</nuxt-link>
                </li>
                <li class="nav-item">
                  <nuxt-link class="nav-link" active-class="active" href="/datenschutz">Datenschutz</nuxt-link>
                </li>
                <!--
                <li class="nav-item">
                  <nuxt-link class="nav-link" active-class="active" href="/nutzungsbedingungen">Nutzungsbedingungen</nuxt-link>
                </li>
                -->
                <li class="nav-item">
                  <nuxt-link class="nav-link" active-class="active" href="/kontakt">Kontakt</nuxt-link>
                </li>
                <li class="nav-item">
                  <nuxt-link class="nav-link" active-class="active" href="/barrierefreiheit">Barrierefreiheit</nuxt-link>
                </li>
              </ul>
            </div>

            <div class="col-6 col-lg-3 project-footer__social">

              <ul class="nav flex-column">
                <li class="nav-item">
                  <a
                    href="https://wisskomm.social/@MaxWeberStiftung"
                    class="nav-link mastodon">
                    <i class="bi bi-mastodon"></i>
                    <span>Mastadon</span>
                  </a>
                </li>
                <li class="nav-item">
                  <a
                    href="https://www.instagram.com/maxweberstiftung/"
                    class="nav-link instagram">
                    <i class="bi bi-instagram"></i>
                    <span>Instagram</span>
                  </a>
                </li>
                <li class="nav-item">
                  <a
                    href="https://de.linkedin.com/company/max-weber-stiftung"
                    class="nav-link linkedin">
                    <i class="bi bi-linkedin"></i>
                    <span>LinkedIn</span>
                  </a>
                </li>
              </ul>

            </div>

            <div class="col-6 col-lg-3 project-footer__perspectivia">
              <a href="https://perspectivia.net/" title="perspectivia.net" class="d-block text-end">
                <nuxt-img
                  preload
                  class="img-fluid"
                  src="/images/perspectivia.net.svg"
                  alt="Logo perspectivia.net" />
              </a>
            </div>

          </div>
        </div>
      </section>
      <section class="project-footer__credits">
        <div class="container">
          <div class="row">

            <div class="col-md-4">
              <div class="copyright_by">
                <a class="mws-link" href="https://www.maxweberstiftung.de">© Max Weber Stiftung 2024</a>
              </div>
            </div>

            <div class="col-md-4">
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

<script setup lang="ts">

  const i18n = useI18n();
  useHead ({
    htmlAttrs: {
      lang: i18n.locale
    }
  })

</script>
