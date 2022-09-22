<template>
  <div class="body-box">

    <header class="project-header">
      <div class="project-header__parent">
        <a href="https://perspectivia.net">
          <img src="/images/perspectivia_logo_p.png" class="" alt="Logo Max-Weber-Stiftung">
        </a>
      </div>
      <div class="project-header__project">
        <h1>
          <a href="/" title="Das Quellen- und Editionsportal der Max Weber Stiftung">
            qed.perspectivia.net
          </a>
        </h1>
        <div class="beta-batch">
          <span class="badge bg-danger">Beta</span>
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
                    <nuxt-link class="nav-link js-close-gpo" active-class="active" href="/">{{ $t('home') }}</nuxt-link>
                  </li>
                  <li class="nav-item">
                    <nuxt-link class="nav-link" active-class="active" href="/suche">{{ $t('globalSearch') }}</nuxt-link>
                  </li>
                  <li class="nav-item">
                    <nuxt-link class="nav-link" active-class="active" href="/ueber">{{ $t('about') }}</nuxt-link>
                  </li>
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
      <section class="project-footer__menu">
        <div class="container">
          <div class="row">
            <div class="col">
              <div class="project-copyright">
                <a href="https://www.maxweberstiftung.de">
                  © Max Weber Stiftung 2022
                </a>
              </div>
            </div>
            <div class="col-auto">
              <nav class="navbar navbar-expand-lg">
                <ul class="navbar-nav">
                  <li class="nav-item">
                    <nuxt-link class="nav-link" active-class="active" href="/impressum">Impressum</nuxt-link>
                  </li>
                  <li class="nav-item">
                    <nuxt-link class="nav-link" active-class="active" href="/datenschutz">Datenschutz</nuxt-link>
                  </li>
                  <li class="nav-item">
                    <nuxt-link class="nav-link" active-class="active" href="/barrierefreiheit">Barrierefreiheit
                    </nuxt-link>
                  </li>
                </ul>
              </nav>
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
                  <img alt="powered by MyCoRe" title="todo:add version number"
                       src="/images/mycore_logo_small_invert.png">
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

</script>
