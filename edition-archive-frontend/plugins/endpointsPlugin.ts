export default defineNuxtPlugin((nuxtApp) => {
    const config = useRuntimeConfig();
    nuxtApp.provide('solrURL', () => {
        if (process.server && !process.dev) {
            return config.public.solrURLServer;
        } else {
            return config.public.solrURL;
        }
    });

    nuxtApp.provide('backendURL', () => {
        if (process.server && !process.dev) {
            return config.public.backendURLServer;
        } else {
            return config.public.backendURL;
        }
    });

    nuxtApp.provide('directusURL', () => {
        if (process.server && !process.dev) {
            return config.public.directusURLServer;
        } else {
            return config.public.directusURL;
        }
    });
})