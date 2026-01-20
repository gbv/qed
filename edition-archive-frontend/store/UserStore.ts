import {defineStore} from "pinia";

// CMS Auth Response Type
interface CMSAuthResult {
  access_token: string;
  token_type: string;
  expires_in?: number; // in seconds
}

// Login Result Type
export interface LoginResult {
  success: boolean;
  error?: 'invalid_credentials' | 'network_error' | 'server_error';
}

export const useUserStore = defineStore('userStore', () => {
  // maxAge auf 24 Stunden setzen - die eigentliche Gültigkeit wird über expires geprüft
  // Das verhindert, dass Cookies als Session-Cookies behandelt werden
  const cookieOptions = { watch: "shallow" as const, maxAge: 60 * 60 * 24 };
  
  const accessTokenCookie = useCookie('access_token', cookieOptions);
  const expiresCookie = useCookie('expires', cookieOptions);
  const userCookie = useCookie('user', cookieOptions);
  
  // Nuxt App für $cmsURL Plugin
  const { $cmsURL } = useNuxtApp();

  // Hilfsfunktion: Extrahiert exp aus JWT Token
  function getTokenExpiration(token: string): number | null {
    try {
      const decoded = jwtDecode(token);
      // exp ist ein Unix-Timestamp in Sekunden, Date.now() gibt Millisekunden zurück
      // Daher: exp * 1000 um beide Werte vergleichbar zu machen
      return decoded.exp ? decoded.exp * 1000 : null;
    } catch {
      return null;
    }
  }

  // Prüft ob der Token abgelaufen ist - mit Puffer für SSR/Client Timing-Unterschiede
  function isExpired(): boolean {
    // Kein Token = nicht abgelaufen (einfach nicht eingeloggt)
    if (!accessTokenCookie.value) {
      return false;
    }
    
    // Versuche zuerst, exp direkt aus dem Token zu lesen (zuverlässiger)
    const tokenExp = getTokenExpiration(accessTokenCookie.value);
    if (tokenExp) {
      // 30 Sekunden Puffer für Netzwerk-Latenz und SSR/Client Zeit-Differenzen
      return Date.now() > (tokenExp - 30000);
    }
    
    // Fallback auf Cookie-basiertes Ablaufdatum
    if (!expiresCookie.value) {
      return true;
    }
    // 30 Sekunden Puffer
    return Date.now() > (parseInt(expiresCookie.value) - 30000);
  }

  // Prüft ob ein gültiger (nicht abgelaufener) Token existiert
  function hasValidToken(): boolean {
    if (!accessTokenCookie.value) {
      return false;
    }
    return !isExpired();
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

  function universalBtoa(str: string): string {
    if (process.server) {
      return Buffer.from(str).toString('base64');
    }
    return btoa(str);
  }

  function jwtDecode(token: string): Record<string, any> {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const text = universalAtob(base64);
    return JSON.parse(text);
  }

  // accessToken computed - OHNE automatisches logout um Side-Effects zu vermeiden
  const accessToken = computed(() => {
    // Nur den Token zurückgeben, keine Logout-Logik hier
    // Die Prüfung auf Gültigkeit erfolgt über hasValidToken()
    return accessTokenCookie.value;
  });

  const expires = computed(() => expiresCookie.value);
  
  const user = computed(() => {
    // Prüfe Token-Gültigkeit ohne Side-Effects
    if (!hasValidToken()) {
      return null;
    }
    if (typeof userCookie.value === "string") {
      return userCookie.value ? JSON.parse(userCookie.value) : null;
    }
    return userCookie.value || null;
  });

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

  // Login mit CMS Basic Auth - intern, setzt Token-Daten
  function setTokenData(authResult: CMSAuthResult, username: string): void {
    accessTokenCookie.value = authResult.access_token;
    
    // Verwende exp aus dem Token wenn möglich, sonst expires_in, sonst 10 Minuten default
    let expiresAt: number;
    const tokenExp = getTokenExpiration(authResult.access_token);
    if (tokenExp) {
      expiresAt = tokenExp;
    } else {
      const expiresIn = (authResult.expires_in || 600) * 1000;
      expiresAt = Date.now() + expiresIn;
    }
    
    expiresCookie.value = expiresAt.toString();
    userCookie.value = JSON.stringify({ name: username });
  }

  // Login - Authentifiziert den Benutzer mit Username und Passwort
  async function login(username: string, password: string): Promise<LoginResult> {
    // Create Basic Auth header
    const credentials = universalBtoa(`${username}:${password}`);
    
    try {
      const response = await fetch(`${$cmsURL()}api/v2/auth/login`, {
        method: 'GET',
        headers: {
          'Authorization': `Basic ${credentials}`
        }
      });

      if (!response.ok) {
        if (response.status === 401) {
          return { success: false, error: 'invalid_credentials' };
        }
        return { success: false, error: 'server_error' };
      }

      const tokenData: CMSAuthResult = await response.json();
      setTokenData(tokenData, username);
      
      // Starte Auto-Refresh nach erfolgreichem Login
      startAutoRefresh();
      
      return { success: true };
    } catch (error) {
      console.error('Login error:', error);
      return { success: false, error: 'network_error' };
    }
  }

  // Update token nach Refresh
  function updateToken(authResult: CMSAuthResult): void {
    accessTokenCookie.value = authResult.access_token;
    
    // Verwende exp aus dem Token wenn möglich
    let expiresAt: number;
    const tokenExp = getTokenExpiration(authResult.access_token);
    if (tokenExp) {
      expiresAt = tokenExp;
    } else {
      const expiresIn = (authResult.expires_in || 600) * 1000;
      expiresAt = Date.now() + expiresIn;
    }
    
    expiresCookie.value = expiresAt.toString();
  }

  // Logout - alle Cookies löschen und Auto-Refresh stoppen
  function logout(): void {
    stopAutoRefresh();
    accessTokenCookie.value = null;
    expiresCookie.value = null;
    userCookie.value = null;
  }
  
  // Prüft und bereinigt abgelaufene Sessions - sollte explizit aufgerufen werden
  function checkAndCleanup(): void {
    if (accessTokenCookie.value && isExpired()) {
      console.log('Token expired, cleaning up session');
      logout();
    }
  }

  // Token refresh - erneuert den Token beim API
  async function refreshToken(): Promise<boolean> {
    if (!hasValidToken()) {
      return false;
    }
    
    try {
      const response = await fetch(`${$cmsURL()}api/v2/auth/renew`, {
        method: 'GET',
        headers: {
          'Accept': 'application/json',
          'Authorization': `Bearer ${accessTokenCookie.value}`
        }
      });
      
      if (response.ok) {
        const data = await response.json();
        if (data.access_token) {
          updateToken(data);
          console.log('Token refreshed successfully');
          return true;
        }
      } else if (response.status === 401) {
        // Token ungültig - ausloggen
        console.log('Token refresh failed: 401 Unauthorized');
        logout();
      }
    } catch (error) {
      console.error('Token refresh failed:', error);
    }
    return false;
  }

  // Auto-Refresh Interval Management
  let refreshInterval: ReturnType<typeof setInterval> | null = null;
  
  function startAutoRefresh(): void {
    // Nur auf Client-Seite und wenn noch kein Interval läuft
    if (process.server || refreshInterval) return;
    
    // Refresh Token alle 4 Minuten (Token läuft nach 10 Min ab)
    refreshInterval = setInterval(() => {
      if (hasValidToken()) {
        refreshToken();
      } else {
        checkAndCleanup();
        stopAutoRefresh();
      }
    }, 1000 * 60 * 4);
    
    console.log('Auto-refresh started');
  }
  
  function stopAutoRefresh(): void {
    if (refreshInterval) {
      clearInterval(refreshInterval);
      refreshInterval = null;
      console.log('Auto-refresh stopped');
    }
  }

  return {
    // State (computed)
    accessToken,
    expires,
    user,
    id,
    // Auth Actions
    login,
    logout,
    refreshToken,
    // Token State Checks
    isExpired,
    hasValidToken,
    isLoggedIn,
    checkAndCleanup,
    // Auto-Refresh Management
    startAutoRefresh,
    stopAutoRefresh
  };
});