import {defineStore} from "pinia";

// CMS Auth Response Type
interface CMSAuthResult {
  access_token: string;
  token_type: string;
  expires_in?: number; // in seconds
}

export const useUserStore = defineStore('userStore', () => {
  // maxAge in Sekunden - 24 Stunden als Standard, damit Cookies nicht als Session-Cookies verloren gehen
  const cookieOptions = { watch: "shallow" as const, maxAge: 60 * 10 };
  
  const accessTokenCookie = useCookie('access_token', cookieOptions);
  const expiresCookie = useCookie('expires', cookieOptions);
  const userCookie = useCookie('user', cookieOptions);

  const accessToken = computed(() => {
    if (isExpired()) {
      logout();
    }
    return accessTokenCookie.value;
  });

  const expires = computed(() => expiresCookie.value);
  
  const user = computed(() => {
    if (isExpired()) {
      return null;
    }
    if (typeof userCookie.value === "string") {
      return userCookie.value ? JSON.parse(userCookie.value) : null;
    }
    return userCookie.value || null;
  });

  // Prüft ob der Token abgelaufen ist
  function isExpired(): boolean {
    // Kein Token = nicht abgelaufen (einfach nicht eingeloggt)
    if (!accessTokenCookie.value) {
      return false;
    }
    // Token vorhanden aber kein Ablaufdatum = als abgelaufen betrachten
    if (!expiresCookie.value) {
      return true;
    }
    return Date.now() > parseInt(expiresCookie.value);
  }

  // Prüft ob ein gültiger (nicht abgelaufener) Token existiert
  function hasValidToken(): boolean {
    return !!accessTokenCookie.value && !!expiresCookie.value && Date.now() <= parseInt(expiresCookie.value);
  }

  // Prüft ob der Benutzer eingeloggt ist
  function isLoggedIn(): boolean {
    return hasValidToken();
  }

  function universalAtob(str: string): string {
    if (process.server) {
      return Buffer.from(str, 'base64').toString();
    }
    return atob(str);
  }

  function jwtDecode(token: string): Record<string, any> {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const text = universalAtob(base64);
    return JSON.parse(text);
  }

  // Benutzer-ID aus dem Token
  const id = computed(() => {
    if (!hasValidToken() || !accessTokenCookie.value) {
      return null;
    }
    try {
      const decoded = jwtDecode(accessTokenCookie.value);
      return decoded.id || decoded.sub || null;
    } catch {
      return null;
    }
  });

  // Login mit CMS Basic Auth
  function login(authResult: CMSAuthResult, username: string): void {
    accessTokenCookie.value = authResult.access_token;
    // Default: 10 Minuten wenn kein expires_in angegeben (basierend auf JWT exp)
    const expiresIn = (authResult.expires_in || 600) * 1000;
    expiresCookie.value = (expiresIn + Date.now()).toString();
    userCookie.value = JSON.stringify({ name: username });
  }

  // Update token nach Refresh
  function updateToken(authResult: CMSAuthResult): void {
    accessTokenCookie.value = authResult.access_token;
    // Default: 10 Minuten wenn kein expires_in angegeben
    const expiresIn = (authResult.expires_in || 600) * 1000;
    expiresCookie.value = (expiresIn + Date.now()).toString();
  }

  // Logout - alle Cookies löschen
  function logout(): void {
    accessTokenCookie.value = null;
    expiresCookie.value = null;
    userCookie.value = null;
  }

  return {
    accessToken,
    expires,
    user,
    id,
    login,
    logout,
    updateToken,
    isExpired,
    hasValidToken,
    isLoggedIn
  };
});