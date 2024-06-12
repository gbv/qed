<template>

  <div :class="`page-headline ${ $route.params.path == '' ? 'page-headline--home' : 'page-headline--hidden' }`">

    <div class="container">
      <div class="row">
        <div class="col">
          <div class="intro-box">
            <h1 class="d-none">perspectivia.net</h1>
            <div class="slogan">
              <div class="slogan__imagebox">
                <img src="https://perspectivia.net/images/backgrounds/PatternStrichpunktGrossKreisViertel.svg" alt="flieder" class="slogan__image">
                <div class="slogan__text">
                  Quellen<br /> und Editionen
                  <div class="slogan__subtext">
                    der Max Weber Stiftung
                  </div>
                </div>

              </div>
            </div>
            <div class="intro-text">
              <p>
                Mit QED stellt die <a href="https://www.maxweberstiftung.de/">Max Weber Stiftung (MWS)</a> unter dem Dach von
                <a href="https://perspectivia.net/">perspectivia.net</a> ein eigenes Portal für die Veröffentlichung von
                digitalen Quellensammlungen und Editionen bereit. Die MWS reagiert damit auf den Bedarf innerhalb der Stiftung,
                für die wachsende Zahl editorisch ausgerichteter Projekte eine stabile, nachhaltige und nutzerfreundliche Lösung
                anzubieten. Der inhaltliche, methodische und konzeptionelle Rahmen, der durch die Projekte aufgespannt wird, ist
                dabei ebenso facettenreich wie die Forschung an den Instituten der Stiftung selbst: international,
                epochenübergreifend und interdisziplinär.
              </p>
              <p class="text-end">
                <a href="/ueber" >
                  Mehr über QED
                </a>
              </p>

            </div>
            <div class="decoration">
              <img src="https://perspectivia.net/images/backgrounds/PatternSchlangenlinenGross.svg" alt="flieder" class="image-embed-item">
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div class="container">
    <div class="row">
      <div class="col external-links">
        <ContentRenderer v-if="data" :value="data"/>
      </div>
    </div>
  </div>


</template>

<script setup lang="ts">
 const {path} = useRoute()

  const {data, error} = await useAsyncData(`content-${path}`, () => {
    const p1 = path.lastIndexOf("/") === path.length - 1 ? path  : path;
    return queryContent().where({_path: p1}).findOne()
  });

  if (error.value) {
    console.error(error.value)
    showError(
        createError({
          statusCode: 404,
          statusMessage: 'Not Found',
        })
    );
  }
</script>

<style scoped>

</style>