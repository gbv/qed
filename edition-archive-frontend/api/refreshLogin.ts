import {type AuthResult} from "@directus/sdk"
import {useUserStore} from "~/store/UserStore";

export async function refreshLogin(): Promise<AuthResult> {
  const {$directusURL} = useNuxtApp();

  const userStore = useUserStore();
  const accessToken = userStore.accessToken;
  const refreshToken = userStore.refreshToken;

  if (!accessToken && !refreshToken) {
    throw new Error("No access or refresh token available");
  }

  const {data, error} = await useFetch($directusURL() + '/auth/refresh', {
    method: 'POST',
    body: JSON.stringify({
      mode: 'json',
      refresh_token: refreshToken
    }),
    headers: {
      'Content-Type': 'application/json'
    }
  });

  let refreshResult = (data.value as any).data as AuthResult;
  userStore.refresh(refreshResult);

  return refreshResult;
}