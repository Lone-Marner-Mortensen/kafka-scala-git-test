#!/bin/bash
#This script will run inside of the kafka-connect container.
#
#All connectors from https://www.confluent.io/hub/ can be installed using:
#confluent-hub install --no-prompt confluentinc/<connector-name>
#Below we install a database-connector
echo "Installing connector plugins"
confluent-hub install --no-prompt confluentinc/kafka-connect-jdbc:5.4.1
#
#All the jars of things we want to connect to are mounted to the jars folder
#Move the jar files to where confluent expect to find them"
mv ../jars/postgresql-42.2.8.jar /usr/share/confluent-hub-components/confluentinc-kafka-connect-jdbc/
#
echo "Launching Kafka Connect worker"
/etc/confluent/docker/run &
#
#Keep container up.
sleep infinity