# apacke-kafka-spring-boot
Producer, consumer concepts using SSL kafka security: 
spring-boot 2.2.6.RELEASE 
wurstmeister/zookeeper and kafka image
Mongo and Mongo-Express

To accelerate my productivity development, i have chosen gradle as my automation build tool.
I have chosen SpringBoot because it is much easier to configure using Java 8.
There is a DockeCompose file whose has two brokers and are exposed using specific port and manually advertise a host name and configure each broker-id

This project has been made to serve as a template, you can easily see the basis concepts to publish and subscribe.
To publish action occurs is necessary to send a message to a http endpoint and the consumer will receive these messages and save in a In Memory Database


#Producer Features
Message --> HTTP-Endpoint --> library-events Topic
Fault-Tolerance: I will save the record in a mongodb instance, in case of an on failure message.
The Producer has a recovery schedule process whose CRON will trigger each minute a search in database and send the failure message again. An message successfully sent will be removed from db

#Consumer Features
library-events Topic Message --> H2
Fault-Tolerance: Here we have a retry policy and a recover process wich saves its failed record in the topic again.
It could lead you to a PoisonPill process, even knowing this i wont cover dead-letter message.
My retry policy tries 3 times in case of a IllegalArgumentException throws, the purpose of this aproach intented to see the mechanic working


valid curl to insert
curl -X POST -H 'Content-type: application/json' -d '{"id":null,"name":"uncle bob"}' http://localhost:9000/v1/book


#SSL
I have introduced SSL kafka security, keep in mind that certs were issued to test in a localhost machine


