#!/usr/bin/env bash

java -cp .\
:res/jar_files/gson/gson-2.2.4.jar\
:res/jar_files/jdbc_connector/mysql-connector-java-5.1.34-bin.jar\
:res/jar_files/json/org.json-20120521.jar\
:res/jar_files/spark/jetty-webapp-7.3.0.v20110203.jar\
:res/jar_files/spark/log4j-1.2.14.jar\
:res/jar_files/spark/servlet-api-3.0.pre4.jar\
:res/jar_files/spark/slf4j-api-1.6.1.jar\
:res/jar_files/spark/slf4j-log4j12-1.6.1.jar\
:res/jar_files/spark/spark-0.9.9.4-SNAPSHOT.jar\
:res/jar_files/spark/spark-0.9.9.4-SNAPSHOT-javadoc.jar\
:res/jar_files/spark/spark-0.9.9.4-SNAPSHOT-sources.jar\
 hu.bme.hit.smartparking.servlet.SmartParkingServlet
