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

const redirectStore = useRedirectStore();
const userStore = useUserStore();

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

  const result = await userStore.login(loginData.username, loginData.password);
  
  if (!result.success) {
    if (result.error === 'invalid_credentials') {
      validate.server = true;
      validate.username = true;
      validate.password = true;
    } else {
      validate.error = true;
    }
    return;
  }

  // Login erfolgreich - weiterleiten
  if (redirectStore.redirectPath) {
    const path = redirectStore.redirectPath;
    redirectStore.clearRedirectPath();
    await navigateTo(path);
  } else {
    await navigateTo('/');
  }
}
</script>

<style scoped>

</style>