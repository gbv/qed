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
        }, 'ditavSolrURL': () => {
          if (import.meta.server && !import.meta.dev) {
            return config.public.ditavSolrURLServer;
          } else {
            return config.public.ditavSolrURL;
          }
        }, 'ditavURL': () => {
          if (import.meta.server && !import.meta.dev) {
            return config.public.ditavURLServer;
          } else {
            return config.public.ditavURL;
          }
        }, 'directusURL': () => {
          if (import.meta.server && !import.meta.dev) {
            return config.public.directusURLServer;
          } else {
            return config.public.directusURL;
          }
        }, 'cmsURL': () => {
          if (import.meta.server && !import.meta.dev) {
            return config.public.cmsServer;
          } else {
            return config.public.cms;
          }
        },
        'backendURL': () => {
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
