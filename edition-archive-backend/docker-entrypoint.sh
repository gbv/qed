#!/usr/bin/bash
set -e
set -o xtrace
echo "Starter Script"
sleep 5 # wait for database (TODO: replace with wait-for-it)

MCR_SAVE_DIR="${MCR_CONFIG_DIR}save/"

MCR_CONFIG_DIR_ESCAPED=$(echo "$MCR_CONFIG_DIR" | sed 's/\//\\\//g')
MCR_DATA_DIR_ESCAPED=$(echo "$MCR_DATA_DIR" | sed 's/\//\\\//g')
MCR_SAVE_DIR_ESCAPED=$(echo "$MCR_SAVE_DIR" | sed 's/\//\\\//g')

SOLR_URL_ESCAPED=$(echo "$SOLR_URL" | sed 's/\//\\\//g')
SOLR_CORE_ESCAPED=$(echo "$SOLR_CORE" | sed 's/\//\\\//g')
SOLR_CLASSIFICATION_CORE_ESCAPED=$(echo "$SOLR_CLASSIFICATION_CORE" | sed 's/\//\\\//g')

JDBC_NAME_ESCAPED=$(echo "$JDBC_NAME" | sed 's/\//\\\//g')
JDBC_PASSWORD_ESCAPED=$(echo "$JDBC_PASSWORD" | sed 's/\//\\\//g')
JDBC_DRIVER_ESCAPED=$(echo "$JDBC_DRIVER" | sed 's/\//\\\//g')
JDBC_URL_ESCAPED=$(echo "$JDBC_URL" | sed 's/\//\\\//g')
HIBERNATE_SCHEMA_ESCAPED=$(echo "$JDBC_URL" | sed 's/\//\\\//g')

MYCORE_PROPERTIES="${MCR_CONFIG_DIR}mycore.properties"
PERSISTENCE_XML="${MCR_CONFIG_DIR}resources/META-INF/persistence.xml"

function downloadDriver {
  FILENAME=$(basename $1)
  if [[ ! -f "${MCR_CONFIG_DIR}lib/$FILENAME" ]]; then
    curl -o "${MCR_CONFIG_DIR}lib/$FILENAME" "$1"
    chmod +x "${MCR_CONFIG_DIR}lib/$FILENAME"
  fi
}

function setDockerValues() {
  echo "Set Docker Values to Config!"
  if [ -n "${SOLR_URL}" ]; then
    sed -ri "s/#?(MCR\.Solr\.ServerURL=).+/\1${SOLR_URL_ESCAPED}/" "${MYCORE_PROPERTIES}"
  fi

  if [ -n "${SOLR_CORE}" ]; then
    sed -ri "s/#?(MCR\.Solr\.Core\.main\.Name=).+/\1${SOLR_CORE_ESCAPED}/" "${MYCORE_PROPERTIES}"
  fi

  if [ -n "${SOLR_CLASSIFICATION_CORE}" ]; then
    sed -ri "s/#?(MCR\.Solr\.Core\.classification\.Name=).+/\1${SOLR_CLASSIFICATION_CORE_ESCAPED}/" "${MYCORE_PROPERTIES}"
  fi

  if [ -n "${JDBC_NAME}" ]; then
    sed -ri "s/(name=\"javax.persistence.jdbc.user\" value=\").*(\")/\1${JDBC_NAME_ESCAPED}\2/" "${PERSISTENCE_XML}"
  fi

  if [ -n "${JDBC_PASSWORD}" ]; then
    sed -ri "s/(name=\"javax.persistence.jdbc.password\" value=\").*(\")/\1${JDBC_PASSWORD_ESCAPED}\2/" "${PERSISTENCE_XML}"
  fi

  if [ -n "${JDBC_DRIVER}" ]; then
    sed -ri "s/(name=\"javax.persistence.jdbc.driver\" value=\").*(\")/\1${JDBC_DRIVER_ESCAPED}\2/" "${PERSISTENCE_XML}"
  fi

  if [ -n "${JDBC_URL}" ]; then
    sed -ri "s/(name=\"javax.persistence.jdbc.url\" value=\").*(\")/\1${JDBC_URL_ESCAPED}\2/" "${PERSISTENCE_XML}"
  fi

  if [ -n "${SOLR_CLASSIFICATION_CORE}" ]; then
    sed -ri "s/(name=\"hibernate.default_schema\" value=\").*(\")/\1${HIBERNATE_SCHEMA_ESCAPED}\2/" "${PERSISTENCE_XML}"
  fi

  sed -ri "s/(name=\"hibernate.hbm2ddl.auto\" value=\").*(\")/\1update\2/" "${PERSISTENCE_XML}"

  if grep -q "MCR.datadir=" "${MYCORE_PROPERTIES}"; then
    sed -ri "s/#?(MCR\.datadir=).+/\1${MCR_DATA_DIR_ESCAPED}/" "${MYCORE_PROPERTIES}"
  else
    echo "MCR.datadir=${MCR_DATA_DIR}" >>"${MYCORE_PROPERTIES}"
  fi

  if grep -q "MCR.Save.FileSystem=" "${MYCORE_PROPERTIES}"; then
    sed -ri "s/#?(MCR\.Save\.FileSystem=).+/\1${MCR_SAVE_DIR_ESCAPED}/" "${MYCORE_PROPERTIES}"
  else
    echo "MCR.Save.FileSystem=${MCR_SAVE_DIR}" >>"${MYCORE_PROPERTIES}"
  fi

  sed -ri "s/<mapping-file>META-INF\/mycore-viewer-mappings.xml<\/mapping-file>//" "${PERSISTENCE_XML}"

  if [ -f "${MCR_CONFIG_DIR}jwt.secret" ]; then
    echo "jwt.secret already exists."
  else
    echo "jwt.secret does not exists, create it.."
    openssl rand -out "${MCR_CONFIG_DIR}jwt.secret" 4096
  fi

  downloadDriver https://repo.maven.apache.org/maven2/net/sf/saxon/Saxon-HE/11.4/Saxon-HE-11.4.jar
}

function setUpMyCoRe {
  /opt/mycore/eab/bin/edition-archive-backend.sh create configuration directory
  setDockerValues
  sed -ri "s/(<\/properties>)/<property name=\"hibernate\.connection\.provider_class\" value=\"org\.hibernate\.connection\.C3P0ConnectionProvider\" \/>\n<property name=\"hibernate\.c3p0\.min_size\" value=\"2\" \/>\n<property name=\"hibernate\.c3p0\.max_size\" value=\"50\" \/>\n<property name=\"hibernate\.c3p0\.acquire_increment\" value=\"2\" \/>\n<property name=\"hibernate\.c3p0\.max_statements\" value=\"30\" \/>\n<property name=\"hibernate\.c3p0\.timeout\" value=\"1800\" \/>\n\1/" "${PERSISTENCE_XML}"
  /opt/mycore/eab/bin/edition-archive-backend.sh init superuser
  /opt/mycore/eab/bin/edition-archive-backend.sh update classification from url http://www.mycore.org/classifications/state.xml
  /opt/mycore/eab/bin/edition-archive-backend.sh update classification from url http://www.mycore.org/classifications/mcr-roles.xml
  /opt/mycore/eab/bin/edition-archive-backend.sh update classification from url http://mycore.de/classifications/derivate_types.xml
  /opt/mycore/eab/bin/edition-archive-backend.sh reload solr configuration main in core main
}

if [[ ! -f "/opt/mycore/eab" ]]; then
    cd /opt/mycore/ &&
    tar -zxvf /opt/mycore/cli.tar.gz &&
    mv edition-archive-backend-cli-*-SNAPSHOT eab &&
    cd -
fi
sed -ri "s/(-DMCR.AppName=).+( \\\\)/\-DMCR.ConfigDir=${MCR_CONFIG_DIR_ESCAPED}\2/" /opt/mycore/eab/bin/edition-archive-backend.sh
sed -ri "s/(-DMCR.ConfigDir=).+( \\\\)/\-DMCR.ConfigDir=${MCR_CONFIG_DIR_ESCAPED}\2/" /opt/mycore/eab/bin/edition-archive-backend.sh

if [ -f "$MYCORE_PROPERTIES" ]; then
  echo "Exists set docker values!"
  setDockerValues
else
  echo "Set up mycore home!"
  setUpMyCoRe
fi

export JAVA_OPTS="-DMCR.ConfigDir=${MCR_CONFIG_DIR} -Xmx${XMX} -Xms${XMS} -XX:+CrashOnOutOfMemoryError -Djavax.xml.transform.TransformerFactory=org.apache.xalan.processor.TransformerFactoryImpl"
catalina.sh run
