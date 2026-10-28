import { getQuery, createError, defineEventHandler } from 'h3';

export default defineEventHandler(async (event) => {
  const config = useRuntimeConfig(event);
  const query = getQuery(event);
  const target = new URL('/servlets/solr/find', config.public.ditavURLServer);

  for (const [key, value] of Object.entries(query)) {
    if (Array.isArray(value)) {
      for (const item of value) {
        target.searchParams.append(key, String(item));
      }
    } else if (value !== undefined && value !== null) {
      target.searchParams.set(key, String(value));
    }
  }

  try {
    return await $fetch(target.toString(), {
      headers: {
        Accept: 'application/json'
      }
    });
  } catch (error: any) {
    throw createError({
      statusCode: error?.statusCode || 502,
      statusMessage: error?.statusMessage || 'Ditav search request failed'
    });
  }
});