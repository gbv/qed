<template>
  <div class="body-box">

    <header class="qed-header">
      <div class="container">

        <div class="qed-header__box">
          <div class="qed-header__box qed-header__box--logo">
            <div class="qed-header__logo">
              <a href="https://perspectivia.net" class="qed-header__logo--p"  title="perspectivia.net Startseite">
                <nuxt-img src="/images/p-logo-solo.svg" preload class="" alt="Logo perspectivia" />
              </a>
              <a href="/"  class="qed-header__logo--qed" title="QED Startseite">
                <nuxt-img src="/images/qed-logo-solo.svg" preload class="" alt="Logo QED" />
              </a>
              <div class="qed-header__logo--beta">
                <nuxt-img src="/images/beta.svg" preload class="" alt="Beta" />
              </div>
            </div>
          </div>

          <div class="qed-header__box qed-header__box--menu">
            <LanguageSelector />
            <div class="qed-header__menu" v-click-outside="closeMenu">
              <nav class="navbar navbar-light w-100 justify-content-end">
                <button
                  class="navbar-toggler"
                  type="button"
                  data-bs-toggle="collapse"
                  data-bs-target="#qed-main-menu"
                  aria-controls="qed-main-menu"
                  aria-expanded="false"
                  aria-label="Toggle navigation">
                  <span class="navbar-toggler__label">{{ $t('qed.mainMenu.menu') }}</span>
                  <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="qed-main-menu" ref="mainmenu">
                  <ul class="navbar-nav w-100 justify-content-end">
                    <li class="nav-item">
                      <nuxt-link
                        class="nav-link"
                        @click="closeMenu"
                        active-class="active"
                        href="/">{{ $t('qed.mainMenu.home') }}
                      </nuxt-link>
                    </li>
                    <li class="nav-item">
                      <nuxt-link
                        class="nav-link"
                        @click="closeMenu"
                        active-class="active"
                        href="/suche">{{ $t('qed.mainMenu.search') }}
                      </nuxt-link>
                    </li>
                    <client-only>
                      <li>
                        <hr class="dropdown-divider">
                      </li>
                      <li class="nav-item">
                        <nuxt-link v-if="!isLoggedIn"
                                   class="nav-link"
                                   active-class="active"
                                   @click="storeRedirect"
                                   to="/login"
                        >{{ $t('qed.mainMenu.login') }}
                        </nuxt-link>
                        <a
                          v-else-if="isClient"
                          class="nav-link"
                          @click.prevent="logout"
                          href="#logout"
                        >{{ $t('qed.mainMenu.logout') }}</a>
                      </li>
                    </client-only>
                    <li><hr class="dropdown-divider"></li>
                    <li class="nav-item">
                      <nuxt-link
                        class="nav-link"
                        @click="closeMenu"
                        active-class="active"
                        href="/ueber">{{ $t('qed.mainMenu.about') }}
                      </nuxt-link>
                    </li>
                  </ul>
                </div>
              </nav>
            </div>
          </div>
        </div>

      </div>
    </header>

    <section class="qed-content content-intro">
      <!--div class="container"-->
        <slot/>
      <!--/div-->
      <button class="btn back-to-top faded" ref="scrolltopbutton" @click="goTop">
        <i class="bi bi-chevron-up"></i>
      </button>
    </section>

    <footer class="qed-footer">
      <section class="qed-footer__nav">
        <div class="container">
          <div class="row">

            <div class="col-12 col-sm-6 col-md-4 col-lg-3 qed-footer__logo">
              <a href="https://www.maxweberstiftung.de">
                <nuxt-img
                  preload
                  src="/images/max-weber-stiftung-de.svg"
                  alt="Logo Max Weber Stiftung" />
              </a>
            </div>

            <div class="col col-sm-6 col-md-4 col-lg-3  qed-footer__menu">
              <ul class="nav flex-column">
                <li class="nav-item">
                  <nuxt-link class="nav-link" active-class="active" href="/impressum">{{ $t('qed.footerMenu.imprint') }}</nuxt-link>
                </li>
                <li class="nav-item">
                  <nuxt-link class="nav-link" active-class="active" href="/datenschutz">{{ $t('qed.footerMenu.privacy') }}</nuxt-link>
                </li>
                <!--
                <li class="nav-item">
                  <nuxt-link class="nav-link" active-class="active" href="/nutzungsbedingungen">{{ $t('qed.footerMenu.terms') }}</nuxt-link>
                </li>
                -->
                <li class="nav-item">
                  <nuxt-link class="nav-link" active-class="active" href="/kontakt">{{ $t('qed.footerMenu.contact') }}</nuxt-link>
                </li>
                <li class="nav-item">
                  <nuxt-link class="nav-link" active-class="active" href="/barrierefreiheit">{{ $t('qed.footerMenu.accessibility') }}</nuxt-link>
                </li>

              </ul>
            </div>

            <div class="col col-sm-6 col-md-4 col-lg-3 qed-footer__social">

              <ul class="nav flex-column">
                <li class="nav-item">
                  <a
                    href="https://wisskomm.social/@MaxWeberStiftung"
                    class="nav-link mastodon">
                    <i class="bi bi-mastodon"></i>
                    <span>Mastodon</span>
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

            <div class="col-12 col-sm-6 col-md-6 offset-md-6 col-lg-3 offset-lg-0 qed-footer__perspectivia">
              <div class="perspectivia-box">
                <a href="https://perspectivia.net/" title="perspectivia.net">
                <nuxt-img
                  preload
                  class="img-fluid"
                  src="/images/perspectivia.net.svg"
                  alt="Logo perspectivia.net" />
                </a>
              </div>
            </div>

          </div>
        </div>
      </section>
      <section class="qed-footer__credits">
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
import {useUserStore} from "~/store/UserStore";
import {useRedirectStore} from "~/store/RedirectStore";


const userStore = useUserStore();
const redirectStore = useRedirectStore();
const router = useRouter();

  const storeRedirect = () => {
    redirectStore.setRedirectPath(router.currentRoute.value.fullPath);
  }

  const i18n = useI18n();
  useHead ({
    htmlAttrs: {
      lang: i18n.locale
    }
  })

  const isClient = computed(() => {
    return process.client;
  });

  const isLoggedIn = computed(() => {
    return !!userStore.accessToken && !userStore.isExpired();
  });

  const logout = () => {
    userStore.logout();
    window.location.reload();
  }

  // hide menu when it is visible and a click happend outside
  // every link in the menu will close the menu also
  const { $bootstrap } = useNuxtApp();
  const mainmenu = ref<HTMLInputElement | null>(null);
  const closeMenu = () => {
    if (mainmenu.value?.classList.contains('show')) {
      $bootstrap.Collapse.getOrCreateInstance(mainmenu.value).hide();
    }
  };

  // method to go to top
  const scrolltopbutton = ref<HTMLInputElement | null>(null);
  const goTop = () => {
    window.scrollTo(0, 0)
  };

  // hide and show go to top button on scrolling
  if (process.client){
    window.addEventListener('scroll', handleScroll);
  }
  function handleScroll(){
    if (window.scrollY > 100) {
      //fade in
      scrolltopbutton.value?.classList.remove('faded');
    } else {
      //fade out
      scrolltopbutton.value?.classList.add("faded");
    }
  }


</script>