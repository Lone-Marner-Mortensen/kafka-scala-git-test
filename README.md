# Kafka Scala Template

This is a template for working with kafka using scala.
Everything is dockerized and all messages are sent in avro format using auto generated avro-code. 
(However, they can easily be changed to another format see below).
This template is tested on two Linux computers having Intellij and gradle installed. 

## Table of Contents

* [What the program does](#What-the-program-does)
* [How to run it](#How-to-run-it)
* [How-to-debug-it](#How-to-debug-it)
* [How to modify/expand it](#How-to-modify/expand-it)

## What the program does
The program produces small "Hello there" messages in the producer and kafka-connect automatically 
transfer them to postgres. The stream module adds !!! in the end of each message. The consumer display
the messages the producer and stream module has made.

## How to run it
The build.gradle file (in the root folder) contains all the necessary commands. If you go to the root-folder of the project and type
gradle -t, then all the default tasks will run (that is prepare, startContainers, producer). You can easily
change the default tasks in the top of build.gradle. Now you can type: docker ps, to see which containers have started.
You should now be able to see that the producer produces messages that are automatically transferred to a postgres container.
Just type: docker exec -it postgres bash -c 'psql -U postgres demo -c "select * from test1"' 
(you can also access the container by typing: psql -h localhost -p 5432 -U postgres, if you have postgres installed. Password = password, database = demo).
If you want to start the stream or consumer container, you just have to type: gradle -t stream or gradle -t consumer. You can see what the
consumer prints by typing: docker-compose -f docker-compose-consumer.yml logs consumer.
#### Troubleshoot: 
If you are using a Macbook, the start-up script docker/scripts/configure-postgres-connection.sh might not work for you. You might need to replace
[ $(expr length "$responseBody") == 0 ] with [ -z "$responseBody" ]. 
If you get errors about port 5432 is already in use or conflicts with other docker-containers.
You can try: sudo service postgresql stop or docker stop <container-name> or docker system prune -a.


## How to debug it
You can see the logs of a container x by typing: docker-compose logs x or docker-compose -f docker-compose-x.yml logs x.
Which command to use depends on how you started the container. You can see in build.gradle how the container was started.
If you want to run a container x in debug mode, you first have to stop the container by typing docker stop x.
Then you have to start the corresponding module in Intellij and attach the env-file in the root-folder called debugIntellij.env
to Intellij debugger.

## How to modify/expand it
### Adding connectors
If you want to add one more connector. You first have to figure out whether it is a source or sink connector.
You can find most connectors on Confluent's website (Confluent is the biggest provider of Kafka components).
Google Confluent and connectors or go to https://www.confluent.io/hub/. Then add the connector jar-file to
docker/kafka-connect-jars and modify the docker/scripts/install-connectors script. Most connectors require some configuration
similar to the postgres-connector (see docker/scripts/configure-postgres-connection script). How to configure the connector is
described on Confluent's website. Remember you also need to add an extra container for what you want to connect to.
### Adding more avro schemas
In order to add a new avro schema go to the avro module and add a file to the avro folder. Then run sbt avro/avroScalaGenerateSpecific
in the root folder. This should auto generate some avro code, which can be found in the target/scala-2.12/src_managed folder of the
avro module. You can use this code in all the modules that depends on the avro module.
### Changing to another format than avro
You would have to change places where avro format is specified.
The producer, consumer and the stream module have a avro-value Serializer/Deserializer. That can be changed
to a string or json -value Serializer/Deserializer. You might also have to change the configuration for a connector.
See docker/scripts/configure-postgres-connection.sh. You only need a schema-registry container, if you are using a format with a schema like avro or json.

