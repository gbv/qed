import {RouterOptions} from "vue-router"
export default defineNuxtPlugin((nuxtApp) => {
  (nuxtApp.$router.options as RouterOptions).scrollBehavior = async (to, from, savedPosition) => {
    let goTo : any | null = null;

    if (to.hash) {
      goTo = {
        el: to.hash,
        behavior: 'smooth',
        top: 64,
      }
    } else if (savedPosition) {
      goTo = savedPosition
    } else {
      goTo = { top: 0, behavior: 'smooth' }
    }

    return new Promise((resolve) => {
      setTimeout(() => {
        resolve(goTo)
      }, 100)
    })
  }
})