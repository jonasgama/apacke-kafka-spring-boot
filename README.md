# apacke-kafka-spring-boot
Producer and consumer concepts using spring-boot 2.2.6.RELEASE and wurstmeister/zookeeper and kafka image

To accelerate my productivity development, i chose gradle as my automation build tool.
I have chosen SpringBoot because it is much easier to configure using Java 8.
There is a DockeCompose file whose has two brokers and are exposed using specific port and manually advertise a host name and configure each broker-id

This project has been made to serve as a template, you can easily see the basis concepts to publish and subscribe.
To publish action occurs is necessary to send a message to a http endpoint and the consumer will receive these messages and save in a In Memory Database


Producer Flow
Message --> HTTP-Endpoint --> library-events Topic


Consumer Flow
library-events Topic Message --> H2


My retry policy tries 3 times in case of a IllegalArgumentException throws, the purpose of this aproach intented to see the mechanic working


valid json to inser
{"libraryEventId":null,"type":"NEW","book":{"id":null,"name":"uncle bob"}}


