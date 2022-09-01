<template>
  <div>
    <div class="row">
      <div class="col">
        <h2 class="mb-1 px-1">Gallia Pontificia Online</h2>
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
                  <a class="nav-link" aria-current="page" href="/gallia_pontifica_online/">Beschreibung</a>
                </li>
                <li class="nav-item dropdown">
                  <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                    Regesten
                  </a>
                  <ul class="dropdown-menu">
                    <li><a class="dropdown-item" href="/gallia_pontifica_online/regesten/reimser/"> Reimser Papstregesten</a></li>
                    <li><a class="dropdown-item" href="/gallia_pontifica_online/regesten/therouanne/">Regesten für Thérouanne</a></li>
                  </ul>
                </li>
                <li class="nav-item">
                  <a class="nav-link" href="/gallia_pontifica_online/publikationen">Publikationen</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" href="/gallia_pontifica_online/erklaerung">Erklärung</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" href="/gallia_pontifica_online/kontakt">Kontakt</a>
                </li>
              </ul>
            </div>
          </div>
        </nav>
      </div>
    </div>
    <slot name="content" />
  </div>
</template>