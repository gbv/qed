import {JSDOM} from "jsdom";

export default defineNuxtPlugin(nuxtApp => {
  return {
    provide: {
      'jsdom': JSDOM
    }
  }
});


