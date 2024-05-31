<template>

  <div :class="`page-headline ${ $route.params.path == '' ? 'page-headline--home' : 'page-headline--'+$route.params.path }`">
    <!--Kopfbild Default {{ $route.params.path }} -->

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
                Die digitale Publikationsplattform perspectivia.net ist ein internationales,
                epochenübergreifendes und interdisziplinäres Portal für geisteswissenschaftliche
                Publikationen. Als Teil der Forschungsinfrastruktur der <a href="https://www.maxweberstiftung.de/">Max Weber Stiftung - Deutsche
                Geisteswissenschaftliche Institute im Ausland</a> werden hier von den Instituten und ihren
                Kooperationspartnern herausgegebene Publikationen und Forschungsdaten frei zugänglich
                gemacht. Die Max Weber Stiftung ist den Prinzipien des Open Access verpflichtet und
                begleitet die Transformation der Geisteswissenschaften in Richtung einer offenen
                Wissenschaft.
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
  console.log("default");
  //document.body.classList.add("basic-page");
</script>

<style scoped>

</style>