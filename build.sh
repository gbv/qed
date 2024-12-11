



case $1 in
backend)
    (cd edition-archive-backend && mvn clean install) &&
    docker compose up --build backend db solr apache
  ;;
frontend)
    docker compose up --build frontend
  ;;
dev-frontend)
  (
    cd edition-archive-frontend && yarn dev
  )
  ;;
all)
    (cd edition-archive-backend && mvn clean install) &&
    docker compose up --build
  ;;
*) ;;
esac
