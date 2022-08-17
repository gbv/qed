FROM tomcat:jdk17-temurin-focal
COPY edition-archive-backend-webapp/target/edition-archive-backend-0.1-SNAPSHOT.war webapps/backend.war
COPY edition-archive-backend-cli/target/edition-archive-backend-cli-0.1-SNAPSHOT.tar.gz /opt/mycore/cli.tar.gz
COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
ENV MCR_CONFIG_DIR="/mcr/home/"
ENV MCR_DATA_DIR="/mcr/data/"
RUN sed -ri "s/<\/Service>/<Connector protocol=\"AJP\/1.3\" tomcatAuthentication=\"false\" scheme=\"http\" secretRequired=\"false\" allowedRequestAttributesPattern=\".*\" encodedSolidusHandling=\"decode\" address=\"0.0.0.0\" port=\"8009\" redirectPort=\"8443\" \/>&/g" /usr/local/tomcat/conf/server.xml
RUN ["chmod", "+x", "/usr/local/bin/docker-entrypoint.sh"]
CMD ["bash", "/usr/local/bin/docker-entrypoint.sh"]