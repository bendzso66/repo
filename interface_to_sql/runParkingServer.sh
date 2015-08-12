#!/bin/sh

java -cp .:jetty-webapp-7.3.0.v20110203.jar                     \
          :log4j-1.2.14.jar:mysql-connector-java-5.1.34-bin.jar \
          :servlet-api-3.0.pre4.jar                             \
          :slf4j-api-1.6.1.jar                                  \
          :slf4j-log4j12-1.6.1.jar                              \
          :spark-0.9.9.4-SNAPSHOT.jar                           \
          :spark-0.9.9.4-SNAPSHOT-javadoc.jar                   \
          :spark-0.9.9.4-SNAPSHOT-sources.jar                   \
          :gson-2.2.4.jar:gson-2.2.4.jar                        \
          :org.json-20120521.jar                                \
    iParkingInterface
