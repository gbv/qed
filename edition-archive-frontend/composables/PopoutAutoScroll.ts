const SCROLL_OBSERVER_MS = 2500;

export function usePopoutAutoScroll(show: Ref<boolean>) {
  const popoutRef = useTemplateRef<HTMLElement>('popoutRef');
  let resizeObserver: ResizeObserver | null = null;
  let resizeObserverTimeout: ReturnType<typeof setTimeout> | null = null;

  const scrollIntoView = () => {
    popoutRef.value?.scrollIntoView({block: 'nearest', behavior: 'smooth'});
  };

  const stop = () => {
    resizeObserver?.disconnect();
    resizeObserver = null;
    if (resizeObserverTimeout != null) {
      clearTimeout(resizeObserverTimeout);
      resizeObserverTimeout = null;
    }
  };

  watch(show, async (open) => {
    if (!open) {
      stop();
      return;
    }
    await nextTick();
    if (!popoutRef.value) return;
    scrollIntoView();
    if (typeof ResizeObserver === 'undefined') return;
    stop();
    resizeObserver = new ResizeObserver(scrollIntoView);
    resizeObserver.observe(popoutRef.value);
    resizeObserverTimeout = setTimeout(stop, SCROLL_OBSERVER_MS);
  });

  onUnmounted(stop);

  return {popoutRef};
}
