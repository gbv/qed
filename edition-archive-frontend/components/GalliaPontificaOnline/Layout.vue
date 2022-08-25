<template>
  <div>
    <div class="row">
      <div class="col">
        <h2 class="mb-5">Gallia Pontificia Online</h2>
      </div>
    </div>
    <div class="row">
      <div class="col-3">
        <GalliaPontificaOnlineMenu/>
      </div>
      <div class="col-9">
        <slot name="content" />
      </div>
    </div>
  </div>
</template>