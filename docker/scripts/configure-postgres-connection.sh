#!/bin/bash
ipPostgres=$(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' postgres)
printf "\nPostgres-IP: ${ipPostgres}\n"

#value.converter and value.converter.schema.registry.url, can be omitted because it is default values.
#value.converter gives the format of the data send to Kafka.

#Don't hardcode user and password, like below in production!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

config="{
"\"connector.class\"": "\"io.confluent.connect.jdbc.JdbcSinkConnector\"",
"\"connection.url\"": "\"jdbc:postgresql://${ipPostgres}:5432/demo\"",
"\"topics\"": "\"test1\"",
"\"key.converter\"": "\"org.apache.kafka.connect.storage.StringConverter\"",
"\"value.converter\""                    : "\"io.confluent.connect.avro.AvroConverter\"",
"\"value.converter.schema.registry.url\"": "\"http://schema-registry:8081\"",
"\"connection.user\"": "\"postgres\"",
"\"connection.password\"": "\"password\"",
"\"auto.create\"": true,
"\"auto.evolve\"": true,
"\"insert.mode\"": "\"upsert\"",
"\"pk.mode\"": "\"record_key\"",
"\"pk.fields\"": "\"MESSAGE_KEY\""
}"

cmd() {
   curl --silent -X PUT http://localhost:8083/connectors/sink-jdbc-postgres/config -H "Content-Type: application/json" -d "$config"
}
responseBody=$(cmd); exitCode=$?
#retry until kafka-connect is ready to get requests
while [ $(expr length "$responseBody") == 0 ]||[ $exitCode != 0 ]; do
  sleep 5
  echo "waiting for connection to kafka-connect"
  responseBody=$(cmd); exitCode=$?
done
echo $responseBody
printf "\nConnection established\n"