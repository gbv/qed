function build_backend() {
  cd edition-archive-backend &&
    mvn clean install &&
    cd -
}

function build_frontend() {
  cd edition-archive-frontend &&
    yarn install &&
    npm run build &&
    cd -
}

case $1 in
backend)
  build_backend &&
    docker-compose up --build backend
  ;;
frontend)
  build_frontend &&
    docker-compose up --build frontend
  ;;
dev-frontend)
  (
    cd edition-archive-frontend && yarn dev
  )
  ;;
all)
  build_backend &&
    build_frontend &&
    docker-compose up --build
  ;;
*) ;;
esac
