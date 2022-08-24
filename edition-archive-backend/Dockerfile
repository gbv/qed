FROM maven:3.8.6-eclipse-temurin-17-focal as maven
RUN mkdir /opt/edition-archive-backend
RUN groupadd maven && \
    useradd -m -g maven maven
COPY . /opt/edition-archive-backend/
RUN chown -R maven:maven /opt/edition-archive-backend/ &&\
    ls -la /opt/edition-archive-backend/
USER maven
WORKDIR /opt/edition-archive-backend
RUN mvn --version && \
    mvn clean install -DskipTests && \
    rm -rf ~/.m2
FROM tomcat:jdk17-temurin-focal
COPY --from=maven --chown=root:root /opt/edition-archive-backend/edition-archive-backend-webapp/target/edition-archive-backend-0.1-SNAPSHOT.war webapps/backend.war
COPY --from=maven --chown=root:root /opt/edition-archive-backend/edition-archive-backend-cli/target/edition-archive-backend-cli-0.1-SNAPSHOT.tar.gz /opt/mycore/cli.tar.gz
COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
ENV MCR_CONFIG_DIR="/mcr/home/"
ENV MCR_DATA_DIR="/mcr/data/"
RUN sed -ri "s/<\/Service>/<Connector protocol=\"AJP\/1.3\" tomcatAuthentication=\"false\" scheme=\"http\" secretRequired=\"false\" allowedRequestAttributesPattern=\".*\" encodedSolidusHandling=\"decode\" address=\"0.0.0.0\" port=\"8009\" redirectPort=\"8443\" \/>&/g" /usr/local/tomcat/conf/server.xml
RUN ["chmod", "+x", "/usr/local/bin/docker-entrypoint.sh"]
CMD ["bash", "/usr/local/bin/docker-entrypoint.sh"]