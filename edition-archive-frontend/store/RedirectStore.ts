export const useRedirectStore = defineStore('redirectStore', () => {
  const redirectPath = ref<string | null>(null);

  function setRedirectPath(path: string) {
    redirectPath.value = path;
  }

  function clearRedirectPath() {
    redirectPath.value = null;
  }

  return {
    redirectPath,
    setRedirectPath,
    clearRedirectPath,
  };
});