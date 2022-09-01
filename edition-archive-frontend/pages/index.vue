<template>
  <div class="row row-cols-1 row-cols-md-3 g-4">
    <div class="col">
      <ContentCard img-href="/images/card-back-golovkin-mikhail.jpg" img-alt="..." :link-text="$t('toProject')"
                    link="http://qed.perspectivia.net/index.php?id=593" card-title="The Languages of Diplomacy">
        This special issue is conceived within the framework of a German-Russian research project “The Languages of
        Diplomacy
        in Russia in the Eighteenth Century in the European Context”
        (2022-2024).
      </ContentCard>
    </div>

    <div class="col">
      <ContentCard img-href="/images/card-back-sovjet.jpg" img-alt="..." :link-text="$t('toProject')"
                    link="https://reposis-test.gbv.de/diemawes/" card-title="Sowjetische Zeitzeugen">
        Diese Website versammelt die ersten aufgezeichneten Stimmen
        von sowjetischen Überlebenden der NS-Herrschaft.
      </ContentCard>
    </div>

    <div class="col">
      <ContentCard img-href="/images/card-back-lettre-pape.png" img-alt="..." :link-text="$t('toProject')"
                    link="gallia_pontifica_online/" card-title="Gallia Pontificia Online">
        Die »Gallia Pontificia« ist das einzige Langzeitprojekt des
        DHIP; sie wird zusammen mit der École nationale des chartes
        bearbeitet. Ziel ist die kritische Edition aller Papsturkunden
        bis zum Pontifikatsbeginn Innocenz’ III. (1198–1216).
      </ContentCard>
    </div>

  </div>
</template>

<script>
  export default {}
</script>
