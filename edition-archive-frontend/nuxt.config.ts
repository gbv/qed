import { defineNuxtConfig } from 'nuxt/config'

// https://v3.nuxtjs.org/api/configuration/nuxt.config
export default defineNuxtConfig({
    modules: [
        '@nuxt/content',
        '@nuxtjs/i18n',
        '@nuxt/image-edge',
        '@pinia/nuxt',
        'nuxt-runtime-compiler'
    ],
    experimental: {
      externalVue: true
    },
    css: [
        '@/assets/scss/prototype.scss',
        "bootstrap-icons/font/bootstrap-icons.css"
    ],
    i18n: {
        langDir: 'locales/',
        lazy: true,
        dynamicRouteParams: false,
        strategy: 'no_prefix',
        detectBrowserLanguage: {
          useCookie: true,
          alwaysRedirect: false,
        },
        locales: [
          { code: 'de', iso: 'de-DE', file: 'de.json', name: 'Deutsch' },
          { code: 'en', iso: 'en-US', file: 'en.json', name: 'English' },
          { code: 'fr', iso: 'fr-FR', file: 'fr.json', name: 'Français' }
        ]
    },
    runtimeConfig: {
        public: {
            backendURL: 'http://localhost:8000/backend/',
            backendURLServer: 'http://backend:8291/backend/',
            solrURL: 'http://localhost:8000/solr/',
            solrURLServer: 'http://solr:8983/solr/',
            directusURL: 'http://localhost:8000/cms/',
            directusURLServer: 'http://cms:8055/',
            sovietSurvivorsURLServer: 'https://qed.perspectivia.net/soviet-survivors-backend/',
            sovietSurvivorsURL: 'https://qed.perspectivia.net/soviet-survivors-backend/',
            sovietSurvivorsSolrURL: 'https://qed.perspectivia.net/soviet-survivors-solr/',
            sovietSurvivorsSolrURLServer: 'https://qed.perspectivia.net/soviet-survivors-solr/'
        }
    },
    typescript: {
        /* strict: true, */
        typeCheck: true
    },
   image: {
      provider: 'ipx',
      presets:{
        ipx:{
          modifiers: {
            format: "webp"
          }
        }
      }
   }
})
//