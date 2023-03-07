<template>
  <li :class="userStore.id ? 'dropdown':''" class="nav-item">
    <template v-if="userStore.id">
      <a aria-expanded="false" class="nav-link dropdown-toggle" data-bs-toggle="dropdown" href="#" role="button">
        <i class="bi bi-person"></i> {{ userStore.user?.first_name || '' }} {{ userStore.user?.last_name || '' }}
      </a>
      <ul class="dropdown-menu">
        <li>
          <a class="dropdown-item" href="#" v-on:click.prevent="userStore.logout()">
            {{ $t('logout') }}
          </a>
        </li>
      </ul>
    </template>
    <nuxt-link v-else :href="`/login?redirect=${route.path.toString()}`" class="nav-link">
      <i class="bi bi-person"></i> {{ $t('login') }}
    </nuxt-link>
  </li>
</template>

<script lang="ts" setup>
import {useUserStore} from "~/store/UserStore";

const route = useRoute()

const userStore = useUserStore();

</script>

<style scoped>

</style>