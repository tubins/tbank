#bin/bash
cd ./accounts
mvn spring-boot:build-image -Dmaven.test.skip=true
cd ../cards
mvn spring-boot:build-image -Dmaven.test.skip=true
cd ../loans
mvn spring-boot:build-image -Dmaven.test.skip=true
cd ../configserver
mvn spring-boot:build-image -Dmaven.test.skip=true
cd ../eurekaserver
mvn spring-boot:build-image -Dmaven.test.skip=true
cd ../gatewayserver
mvn spring-boot:build-image -Dmaven.test.skip=true
cd ../
