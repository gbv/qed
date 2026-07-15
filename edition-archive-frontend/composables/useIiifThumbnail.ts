import type {MaybeRefOrGetter} from "vue";

export interface UseIiifThumbnailOptions {
  /** IIIF size parameter for the thumbnail image, e.g. "!300,300". */
  size?: string;
  /**
   * Gate for the info.json request: it is only sent once this turns truthy. Use it to defer
   * loading until the thumbnail is actually visible. Defaults to eager loading.
   */
  enabled?: MaybeRefOrGetter<boolean>;
}

/**
 * Loads a MyCoRe IIIF thumbnail's info.json to determine whether a renderable image exists
 * for the given object/derivate id. The request goes through Nuxt's {@link useFetch}, so it is
 * cached and deduped by id across component instances.
 *
 * The MyCoRe thumbnail service returns a valid IIIF info.json (with a numeric `width`) only when
 * an image is available; otherwise it answers with a non-2xx status. Consumers should render the
 * thumbnail only when `exists` is true and simply show nothing otherwise.
 *
 * @param appUrl base application URL (must end with a slash), e.g. "https://ditav-test.gbv.de/"
 * @param id     the mods object or derivate id whose main image should be shown
 */
export function useIiifThumbnail(
  appUrl: MaybeRefOrGetter<string>,
  id: MaybeRefOrGetter<string>,
  options: UseIiifThumbnailOptions = {}
) {
  const size = options.size ?? "!300,300";
  const base = computed(() => `${toValue(appUrl)}api/iiif/image/v2/thumbnail/${toValue(id)}`);
  const infoURL = computed(() => `${base.value}/info.json`);
  const thumbnailURL = computed(() => `${base.value}/full/${size}/0/default.jpg`);

  const enabled = options.enabled ?? true;
  const {data, status, execute} = useFetch(infoURL, {
    key: () => `iiif-thumb-${toValue(id)}`,
    server: false,
    lazy: true,
    // defer the request; it is fired by the watcher below once `enabled` is truthy
    immediate: false,
    // a missing image answers with a non-2xx status; swallow it so the caller can just
    // hide the thumbnail instead of surfacing an error
    ignoreResponseError: true,
  });

  watch(() => toValue(enabled), (on) => {
    if (on && status.value === "idle") {
      execute();
    }
  }, {immediate: true});

  // a valid IIIF info.json always carries a numeric width; an error body does not
  const exists = computed(() => typeof (data.value as any)?.width === "number");

  return {exists, thumbnailURL, infoURL};
}
