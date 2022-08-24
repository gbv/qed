



case $1 in
backend)
    docker-compose up --build backend
  ;;
frontend)
    docker-compose up --build frontend
  ;;
dev-frontend)
  (
    cd edition-archive-frontend && yarn dev
  )
  ;;
all)
    docker-compose up --build
  ;;
*) ;;
esac
