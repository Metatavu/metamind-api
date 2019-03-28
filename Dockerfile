FROM jboss/wildfly:16.0.0.Final

ADD --chown=jboss target/*.war /opt/jboss/standalone/deployments/app.war
ADD --chown=jboss ./docker/entrypoint.sh /opt/docker/entrypoint.sh 
ADD --chown=jboss ./docker/host.cli /opt/docker/host.cli
ADD --chown=jboss ./docker/kubernets-jgroups.cli /opt/docker/kubernets-jgroups.cli
ADD --chown=jboss ./docker/jdbc.cli /opt/docker/jdbc.cli
RUN chmod a+x /opt/docker/entrypoint.sh

ARG WILDFLY_VERSION=16.0.0.Final
ARG MARIADB_MODULE_VERSION=2.3.0
ARG KUBERNETES_MODULE_VERSION=0.9.3

RUN curl -o /tmp/mariadb-module.zip -L https://static.metatavu.io/wildfly/wildfly-${WILDFLY_VERSION}-jgroup-kubernetes-module-${KUBERNETES_MODULE_VERSION}.zip
RUN curl -o /tmp/kubernetes-module.zip -L https://static.metatavu.io/wildfly/mariadb-module-${MARIADB_MODULE_VERSION}.zip

RUN unzip -o /tmp/mariadb-module.zip -d /opt/jboss/wildfly/
RUN unzip -o /tmp/kubernetes-module.zip -d /opt/jboss/wildfly/
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/opt/docker/host.cli
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/opt/docker/jdbc.cli
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/opt/docker/kubernets-jgroups.cli

EXPOSE 8080

CMD "/opt/docker/entrypoint.sh"