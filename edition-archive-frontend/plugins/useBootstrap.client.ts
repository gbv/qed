import * as bootstrap from 'bootstrap/dist/js/bootstrap.bundle';
export default defineNuxtPlugin(nuxtApp => {
  return {
    provide: {
      'bootstrap': bootstrap
    }
  }
});


