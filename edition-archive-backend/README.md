
# edition-archive-backend

## Installation Instructions

* clone repository
* run `mvn clean install`
* unzip edition-archive-backend-cli to user defined cli directory
* change to cli directory
* run `bin/edition-archive-backend.sh create configuration directory`
* you now have a config dir `~/.mycore/edition-archive-backend`
* configure your database connection in `~/.mycore/edition-archive-backend/resources/META-INF/persistence.xml`
* perhaps you need to download a driver to `~/.mycore/edition-archive-backend/lib/`
* run cli command `bin/edition-archive-backend.sh process config/setup-commands.txt to load default data`
* Go to edition-archive-backend-webapp
* Install solr with the command: `mvn solr-runner:copyHome`
* Run solr with the command `mvn solr-runner:start` (End with `mvn solr-runner:stop`)
* Run Jetty with the command: `mvn jetty:run` (End with ctrl+c)
* Fast rebuild and Jetty restart `mvn clean install && cd edition-archive-backend-module && mvn jetty:run` (End with ctrl+c)