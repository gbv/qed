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
import regests from cei file /mcr/tmp/xml.xml and csv /mcr/tmp/csv.csv
```

## Backup the cms

The instance of the restore cms should not have any entries in the database. The script will not delete any entries.
```bash
./backup_cms.sh backup usermail userpass http://localhost:8000/cms ./import/cms/

./backup_cms.sh restore usermail userpass http://localhost:8000/cms ./import/cms/
```