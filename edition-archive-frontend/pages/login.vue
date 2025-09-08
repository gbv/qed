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
              <div class="form-group">
                <label for="email">{{ $t('login.email') }}</label>
                <input id="email" v-model="loginData.email" :class="!validate.server && !validate.email ? '':'is-invalid'"
                       class="form-control" type="email">
              </div>
              <div class="form-group">
                <label for="password">{{ $t('login.password') }}</label>
                <input id="password" v-model="loginData.password"
                       :class="!validate.server && !validate.email ? '':'is-invalid'"
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
import {type AuthResult, type UserType} from "@directus/sdk"

const {$directusURL} = useNuxtApp();
const {query} = useRoute()

const store = useUserStore();

let loginData = reactive({
  email: '',
  password: ''
});

let validate = reactive({
  email: false,
  password: false,
  server: false,
  error: false
});

const emailChanged = () => {
  validate.email = false;
  validate.server = false;
}

const passwordChanged = () => {
  validate.password = false;
  validate.server = false;
}

const userLogin = async () => {
  validate.email = false;
  validate.password = false;
  validate.server = false;
  validate.error = false;

  if ((loginData.email || "").trim().length == 0) {
    validate.email = true;
    return;
  }
  if ((loginData.password || "").trim().length == 0) {
    validate.password = true;
    return;
  }
  const {data, error} = await useFetch($directusURL() + '/auth/login', {
    method: 'POST',
    body: JSON.stringify(loginData),
    headers: {
      'Content-Type': 'application/json'
    }
  });

  if (error.value) {
    if (error.value.statusCode) {
      if (error.value.statusCode === 401) {
        validate.server = true;
        validate.email = true;
        validate.password = true;

      } else {
        validate.error = true;
      }
    }
    console.error(error.value);
    return;
  }

  const tokenData = ((data.value as any).data as AuthResult);

  const userResp = await useFetch($directusURL() + '/users/me', {
    headers: {
      'Authorization': `Bearer ${tokenData.access_token}`
    }
  });

  if (userResp.error.value) {
    console.error(userResp.error.value);
    return;
  }

  const userData = ((userResp.data.value as any).data) as UserType;
  store.login(tokenData, userData);

  if (query.redirect) {
    await navigateTo(`${query.redirect}`);
  } else {
    await navigateTo('/');
  }
}
</script>

<style scoped>

</style>