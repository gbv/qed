<template>
  <div>
    <div class="row">
      <div class="col">
        <h2 class="mb-1 px-1">Gallia Pontificia online</h2>
      </div>
    </div>
    <div class="row">
      <div class="col">
        <nav class="navbar navbar-expand-lg mb-5">
          <div class="container-fluid">
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
              <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarSupportedContent">
              <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                  <nuxt-link class="nav-link" aria-current="page" href="/gallia-pontifica-online/">Zum Projekt</nuxt-link>
                </li>
                <li class="nav-item dropdown">
                  <nuxt-link class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                    Regesten
                  </nuxt-link>
                  <ul class="dropdown-menu">
                    <li><nuxt-link class="dropdown-item" active-class="active" href="/gallia-pontifica-online/regesten/reims/">Reims I: Erzbischöfe</nuxt-link></li>
                    <li><nuxt-link class="dropdown-item" active-class="active" href="/gallia-pontifica-online/regesten/genf/">Genf I: Bischöfe</nuxt-link></li>
                  </ul>
                </li>
                <li class="nav-item">
                  <nuxt-link class="nav-link" active-class="active" href="/gallia-pontifica-online/editionen">Editionen</nuxt-link>
                </li>
                <li class="nav-item">
                  <nuxt-link class="nav-link" active-class="active" href="/gallia-pontifica-online/publikationen">Publikationen</nuxt-link>
                </li>
                <li class="nav-item">
                  <nuxt-link class="nav-link" active-class="active" href="/gallia-pontifica-online/kontakt">Kontakt</nuxt-link>
                </li>
              </ul>
            </div>
          </div>
        </nav>
      </div>
    </div>

    <div class="row" v-if="$route.params.regesten == 'reims' || $route.params.regesten == 'genf'">
      <div class="col-12 mb-5">
        <h2>
          <span v-if="$route.params.regesten == 'reims'">Reims I: Erzbischöfe</span>
          <span v-else-if="$route.params.regesten == 'genf'">Genf I: Bischöfe</span>
          <span v-else>Subpage</span>
        </h2>
      </div>
      <div class="col-3">
        <slot name="menu" />
      </div>
      <div class="col-9">
        <slot name="content" />
      </div>
    </div>

    <div v-else>
      <slot name="content" />
    </div>

  </div>
</template>
