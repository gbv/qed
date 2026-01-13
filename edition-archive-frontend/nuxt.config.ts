import { defineNuxtConfig } from 'nuxt/config'

export default defineNuxtConfig({
    modules: ['@nuxt/content', '@nuxtjs/i18n', '@pinia/nuxt', '@nuxt/image'],
    experimental: {
      externalVue: true
    },
    css: [
        '@/assets/scss/prototype.scss',
        "bootstrap-icons/font/bootstrap-icons.css"
    ],
    i18n: {
        lazy: true,
        strategy: 'no_prefix',
        bundle: {
          optimizeTranslationDirective: false
        },
        detectBrowserLanguage: {
          useCookie: true,
          alwaysRedirect: false,
        },
        locales: [
          { code: 'de', file: 'de.json', name: 'Deutsch' },
          { code: 'en', file: 'en.json', name: 'English' },
          { code: 'fr', file: 'fr.json', name: 'Français' }
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
            sovietSurvivorsSolrURLServer: 'https://qed.perspectivia.net/soviet-survivors-solr/',
            ditavURL: 'https://ditav-test.gbv.de/',
            ditavURLServer: 'https://ditav-test.gbv.de/',
            ditavSolrURL: 'https://ditav-test.gbv.de/api/v1/search/',
            ditavSolrURLServer: 'https://ditav-test.gbv.de/api/v1/search/',
            geonamesUsername: 'qedperspectivia'
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
   },
  vue: {
      runtimeCompiler: true,
  }
})
//
