FROM quay.io/wildfly/wildfly:39.0.1.Final-2-jdk17

COPY wildfly-conf/application-users.properties  /opt/jboss/wildfly/standalone/configuration/application-users.properties
COPY wildfly-conf/application-roles.properties  /opt/jboss/wildfly/standalone/configuration/application-roles.properties
COPY wildfly-conf/mgmt-users.properties         /opt/jboss/wildfly/standalone/configuration/mgmt-users.properties
COPY wildfly-conf/mgmt-groups.properties        /opt/jboss/wildfly/standalone/configuration/mgmt-groups.properties

EXPOSE 8080 9990

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
