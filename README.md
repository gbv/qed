# Build

There is a build script which helps you to quick start:

## ./build.sh all
Starts the backend and the frontend

## ./build.sh backend
Starts the backend

## ./build.sh frontend
Starts the frontend

## ./build.sh dev-frontend
Starts the frontend with live changes

## import regest file
copy the current regest file from `import/*.xml` to `docker/backend-tmp/xml.xml`
At localhost:8000/backend/ you can login and start the webcli and run the command:
``` 
import regests from cei file /mcr/tmp/xml.xml
```