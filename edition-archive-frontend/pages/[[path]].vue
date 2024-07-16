<template>

  <div :class="`page-headline ${ $route.params.path == '' ? 'page-headline--home' : 'page-headline--hidden' }`">

    <div class="container">
      <div class="row">
        <div class="col">
          <div class="intro-box">
            <div class="slogan">
              <div class="slogan__imagebox">
                <img src="https://perspectivia.net/images/backgrounds/PatternStrichpunktGrossKreisViertel.svg" alt="flieder" class="slogan__image">
                <div class="slogan__textbox">
                  <h1 class="d-none d-xl-block">
                    <div class="slogan__text" v-if="i18n.locale.value == 'en'">
                      Sources<br /> and Editions
                      <div class="slogan__subtext">
                        of the Max Weber Foundation
                      </div>
                    </div>
                    <div class="slogan__text" v-else-if="i18n.locale.value == 'fr'">
                      Sources<br /> et éditions
                      <div class="slogan__subtext">
                        de la Fondation Max Weber
                      </div>
                    </div>
                    <div class="slogan__text" v-else>
                      Quellen<br /> und Editionen
                      <div class="slogan__subtext">
                        der Max Weber Stiftung
                      </div>
                    </div>
                  </h1>
                </div>
              </div>
            </div>
            <div class="intro-text">
              <h1 class="d-block d-xl-none mb-3">
                <span v-if="i18n.locale.value == 'en'">Sources and Editions of the Max Weber Foundation</span>
                <span v-else-if="i18n.locale.value == 'fr'">Sources et éditions de la Fondation Max Weber</span>
                <span v-else>Quellen und Editionen der Max Weber Stiftung</span>
              </h1>

              <p v-if="i18n.locale.value == 'en'">
                With QED, the <a href="https://www.maxweberstiftung.de/">Max Weber Foundation (MWS)</a> is providing its own
                portal for the publication of digital source collections and editions under the umbrella of
                <a href="https://perspectivia.net/">perspectivia.net</a>. The MWS is thus responding to the need within the
                Foundation to offer a stable, sustainable and user-friendly solution for the growing number of editorially
                orientated projects. The content, methodological and conceptual framework provided by the projects is just
                as multifaceted as the research at the Foundation's institutes themselves: international, cross-epochal and
                interdisciplinary.
              </p>
              <p v-else-if="i18n.locale.value == 'fr'">
                Avec QED, <a href="https://www.maxweberstiftung.de/">la Fondation Max Weber (MWS)</a> met à disposition,
                sous l'égide de <a href="https://perspectivia.net/">perspectivia.net</a>, son propre portail pour la publication
                de collections de sources et d'éditions numériques. La MWS répond ainsi à la nécessité, au sein de la fondation,
                de proposer une solution stable, durable et conviviale pour le nombre croissant de projets à vocation éditoriale.
                Le cadre thématique, méthodologique et conceptuel dans lequel s'inscrivent les projets est aussi diversifié que
                la recherche menée dans les instituts de la fondation : international, inter-époques et interdisciplinaire.
              </p>
              <p v-else>
                Mit QED stellt die <a href="https://www.maxweberstiftung.de/">Max Weber Stiftung (MWS)</a> unter dem Dach von
                <a href="https://perspectivia.net/">perspectivia.net</a> ein eigenes Portal für die Veröffentlichung von
                digitalen Quellensammlungen und Editionen bereit. Die MWS reagiert damit auf den Bedarf innerhalb der Stiftung,
                für die wachsende Zahl editorisch ausgerichteter Projekte eine stabile, nachhaltige und nutzerfreundliche Lösung
                anzubieten. Der inhaltliche, methodische und konzeptionelle Rahmen, der durch die Projekte aufgespannt wird, ist
                dabei ebenso facettenreich wie die Forschung an den Instituten der Stiftung selbst: international,
                epochenübergreifend und interdisziplinär.
              </p>
              <p>
                <a
                  href="/ueber"
                  class="btn btn-primary text-white no-external-mark mt-3">
                  {{ $t("qed.homePage.more") }}
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
  import {useI18n} from "vue-i18n";
  const i18n = useI18n();
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
