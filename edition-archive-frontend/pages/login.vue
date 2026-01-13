<template>
  <div class="container">
    <div class="row">
      <div class="col">
        <div class="card">
          <div class="card-header">
            <h3>{{ $t("login.title") }}</h3>
          </div>
          <div class="card-body">
            <form @submit.prevent="userLogin">
              <div class="form-group mb-3">
                <label for="username">{{ $t('login.username') }}</label>
                <input id="username" v-model="loginData.username" :class="!validate.server && !validate.username ? '':'is-invalid'"
                       class="form-control" type="text">
              </div>
              <div class="form-group mb-3">
                <label for="password">{{ $t('login.password') }}</label>
                <input id="password" v-model="loginData.password"
                       :class="!validate.server && !validate.password ? '':'is-invalid'"
                       class="form-control" type="password">
              </div>
              <div v-if="validate.server" class="alert alert-danger">{{ $t("login.invalid") }}</div>
              <div v-if="validate.error" class="alert alert-danger">{{ $t("login.error") }}</div>
              <button class="btn btn-primary" type="submit">{{ $t("login.submit") }}</button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {useUserStore} from "~/store/UserStore";
import {useRedirectStore} from "~/store/RedirectStore";

const {$cmsURL} = useNuxtApp();

const redirectStore = useRedirectStore();
const store = useUserStore();

let loginData = reactive({
  username: '',
  password: ''
});

let validate = reactive({
  username: false,
  password: false,
  server: false,
  error: false
});

const userLogin = async () => {
  validate.username = false;
  validate.password = false;
  validate.server = false;
  validate.error = false;

  if ((loginData.username || "").trim().length == 0) {
    validate.username = true;
    return;
  }
  if ((loginData.password || "").trim().length == 0) {
    validate.password = true;
    return;
  }

  // Create Basic Auth header
  const credentials = btoa(`${loginData.username}:${loginData.password}`);
  
  try {
    const response = await fetch(`${$cmsURL()}api/v2/auth/login`, {
      method: 'GET',
      headers: {
        'Authorization': `Basic ${credentials}`
      }
    });

    if (!response.ok) {
      if (response.status === 401) {
        validate.server = true;
        validate.username = true;
        validate.password = true;
      } else {
        validate.error = true;
      }
      console.error('Login failed:', response.status, response.statusText);
      return;
    }

    const tokenData = await response.json();
    
    // Store the token
    store.login(tokenData, loginData.username);

    if(redirectStore.redirectPath) {
      const path = redirectStore.redirectPath;
      redirectStore.clearRedirectPath()
      await navigateTo(path);
    } else {
      await navigateTo('/');
    }
  } catch (err) {
    console.error('Login error:', err);
    validate.error = true;
  }
}
</script>

<style scoped>

</style>