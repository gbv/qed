export default defineNuxtPlugin({
  name: 'endpointsPlugin', setup: (nuxtApp) => {
    const config = useRuntimeConfig();
    return {
      provide: {
        'solrURL': () => {
          if (import.meta.server && !import.meta.dev) {
            return config.public.solrURLServer;
          } else {
            return config.public.solrURL;
          }
        }, 'sovietSurvivorsSolrURL': () => {
          if (import.meta.server && !import.meta.dev) {
            return config.public.sovietSurvivorsSolrURLServer;
          } else {
            return config.public.sovietSurvivorsSolrURL;
          }
        }, 'sovietSurviorsURL': () => {
          if (import.meta.server && !import.meta.dev) {
            return config.public.sovietSurvivorsURLServer;
          } else {
            return config.public.sovietSurvivorsURL;
          }
        }, 'gazinSolrURL': () => {
          if (import.meta.server && !import.meta.dev) {
            return config.public.gazinSolrURLServer;
          } else {
            return config.public.gazinSolrURL;
          }
        }, 'gazinURL': () => {
          if (import.meta.server && !import.meta.dev) {
            return config.public.gazinURLServer;
          } else {
            return config.public.gazinURL;
          }
        }, 'directusURL': () => {
          if (import.meta.server && !import.meta.dev) {
            return config.public.directusURLServer;
          } else {
            return config.public.directusURL;
          }
        }, 'backendURL': () => {
          if (import.meta.server && !import.meta.dev) {
            return config.public.backendURLServer;
          } else {
            return config.public.backendURL;
          }
        }
      }
    }
  }
})
