BASE=~/.m2/repository

KLASSPATH=$BASE/org/cometd/java/bayeux-api/3.0.9/bayeux-api-3.0.9.jar:$BASE/org/cometd/java/cometd-java-common/3.0.9/cometd-java-common-3.0.9.jar:$BASE/org/cometd/java/cometd-java-client/3.0.9/cometd-java-client-3.0.9.jar:$BASE/org/eclipse/jetty/jetty-client/9.2.14.v20151106/jetty-client-9.2.14.v20151106.jar:$BASE/org/eclipse/jetty/jetty-util/9.2.14.v20151106/jetty-util-9.2.14.v20151106.jar

javac -classpath $KLASSPATH src/vr_logorrhoe/FayeClient.java

(cd src/vr_logorrhoe && java FayeClient)
