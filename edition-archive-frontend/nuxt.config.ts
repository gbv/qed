import { defineNuxtConfig } from 'nuxt'

// https://v3.nuxtjs.org/api/configuration/nuxt.config
export default defineNuxtConfig({
    modules: [
        '@nuxt/content',
        '@intlify/nuxt3'
    ],
    css: [
        '@/assets/prototype.scss'
    ],
    intlify: {
        localeDir: 'locales', // set the `locales` directory at source directory of your Nuxt application
        vueI18n: {
            locale: 'de'
            // ...
        }
    }

})
