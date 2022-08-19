import { defineNuxtConfig } from 'nuxt'

// https://v3.nuxtjs.org/api/configuration/nuxt.config
export default defineNuxtConfig({
    modules: [
        '@nuxt/content',
        '@intlify/nuxt3'
    ],
    css: [
        '@/assets/scss/prototype.scss'
    ],
    intlify: {
        localeDir: 'locales', // set the `locales` directory at source directory of your Nuxt application
        vueI18n: {
            locale: 'de'
        }
    },
    runtimeConfig: {
        public: {
            backendURL: 'http://localhost:8000/backend/',
            backendURLServer: 'http://backend:8291/backend/',
            solrURL: 'http://localhost:8000/solr/',
            solrURLServer: 'http://solr:8983/solr/'
        }
    }

})
//