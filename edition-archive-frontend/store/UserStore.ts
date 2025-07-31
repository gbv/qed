import {defineStore} from "pinia";
import {type AuthResult, type UserType} from "@directus/sdk"


export const useUserStore = defineStore('userStore', () => {
  const accessTokenCookie = useCookie('access_token', {watch: "shallow"});
  const refreshTokenCookie = useCookie('refresh_token', {watch: "shallow"});
  const expiresCookie = useCookie('expires', {watch: "shallow"});
  const userCookie = useCookie('user', {watch: "shallow"});


  const accessToken = computed(() => {
    if(isExpired()){
      logout();
    }
    return accessTokenCookie.value;
  });

  const refreshToken = computed(() => refreshTokenCookie.value);
  const expires = computed(() => expiresCookie.value);
  const user = computed(() => {
    clearIfExpired();
    if (typeof userCookie.value === "string") {
      return userCookie.value ? JSON.parse(userCookie.value) : null;
    } else {
      return userCookie.value || null;
    }
  });

  function isExpired() {
    if (expires.value) {
      return new Date().valueOf() > parseInt(expires.value);
    } else {
      return true;
    }
  }

  function clearIfExpired() {
    if ((expires.value != null || accessToken.value != null || user != null) && isExpired()) {
      logout();
    }
  }

  function universalAtob(str: string) {
    if (process.server) {
      return Buffer.from(str, 'base64').toString();
    } else {
      return atob(str).toString();
    }
  }

  function jwtDecode(token: string) {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace('-', '+').replace('_', '/');
    const text = universalAtob(base64);
    return JSON.parse(text);
  }

  const id = computed(() => {
    if (accessToken.value) {
      const decoded = jwtDecode(accessToken.value);
      return decoded.id;
    } else {
      return null;
    }
  });

  function deleteToken() {
    accessTokenCookie.value = null;
    refreshTokenCookie.value = null;
    expiresCookie.value = null;
  }

  function login(loginResponse: AuthResult, userResult: UserType) {
    accessTokenCookie.value = loginResponse.access_token;
    refreshTokenCookie.value = loginResponse.refresh_token || null;
    expiresCookie.value = (loginResponse.expires + new Date().valueOf()).toString();
    userCookie.value = JSON.stringify(userResult);
  }

  function refresh(refreshResponse: AuthResult) {
    accessTokenCookie.value = refreshResponse.access_token;
    refreshTokenCookie.value = refreshResponse.refresh_token || null;
    expiresCookie.value = (refreshResponse.expires + new Date().valueOf()).toString();
  }

  function logout() {
    deleteToken();
    userCookie.value = null;
  }

  return {accessToken, refreshToken, expires, deleteToken, login, id, user, refresh, logout}
});