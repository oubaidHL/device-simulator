# Device simulator

This java project (spring boot) simulates an A/C iot device  
It will connect to an Azure IoT Hub via the device provisioning service using the certificate stored in `resources/certs`

Once connected the device will run a list of tasks configured in `com.maibornwolff.esprit.iotschool.IotDevice.createTasks`  
Every task will get the current version of the device twin as input and can make changes to it (e.g. set the new temperature)

# Exercises
The exercises are marked with TODOs, like "TODO exercise 1: ..."  
And are related to the tasks the simulated device will perform.

You can check the entrypoint to those tasks in `com.maibornwolff.esprit.iotschool.IotDevice.createTasks`

# Running locally

We recommend running your app via IntelliJ in debug mode so you can see and investigate what is happening. 

## IDE
In IntelliJ go to the `IotSchoolApplication.java` file and click the run button next to the `public class IotSchoolApplication {` line

## CLI
Run 
```bash
mvn org.springframework.boot:spring-boot-maven-plugin:run -Dspring-boot.run.arguments=--server.port=5001
```
to start the application from your terminal

**NOTE:**  
Make sure you have java version 17 installed for this to work  
If you have multiple versions of java installed, setting `JAVA_HOME` before the command might be required, like this
```bash
JAVA_HOME=/home/path/to/our/jdks/.jdks/temurin-17.0.5 mvn org.springframework.boot:spring-boot-maven-plugin:run -Dspring-boot.run.arguments="--server.port=5001 --cert.name=device" 
```


# Running multiple devices
Once you are confident with your code and want to simulate more than one device, we recommend running them via the command line.  
[Another option would be to copy the "device-simulator" directory multiple times and open each of them in a separate IntelliJ window, but that gets confusing quickly and they will run out of sync once you made code changes] 

First you need to create multiple device certificates, using
```bash
./device.sh tunis-lac2 station-airport-route
./device.sh tunis-lac2 station-city-route
./device.sh tunis-lac2 station-beach-route
```
and then copy them to the resources directory, e.g.
```bash
cp device/tunis-lac2-station-airport-route.key ~/git/iotschool/tunis-iot-school/device-provisioning/src/main/resources/certs/
cp device/tunis-lac2-station-airport-route.pem ~/git/iotschool/tunis-iot-school/device-provisioning/src/main/resources/certs/

cp device/tunis-lac2-station-city-route.key ~/git/iotschool/tunis-iot-school/device-provisioning/src/main/resources/certs/
cp device/tunis-lac2-station-city-route.pem ~/git/iotschool/tunis-iot-school/device-provisioning/src/main/resources/certs/

cp device/tunis-lac2-station-beach-route.key ~/git/iotschool/tunis-iot-school/device-provisioning/src/main/resources/certs/
cp device/tunis-lac2-station-beach-route.pem ~/git/iotschool/tunis-iot-school/device-provisioning/src/main/resources/certs/
```

Then you can run multiple simulated device via
```bash
mvn org.springframework.boot:spring-boot-maven-plugin:run -Dspring-boot.run.arguments="--server.port=7000 --cert.name=tunis-lac2-airport-airport-route --device.version=v2.0.0 --device.route=tunis-lac2-station-airport-route"
mvn org.springframework.boot:spring-boot-maven-plugin:run -Dspring-boot.run.arguments="--server.port=7001 --cert.name=tunis-lac2-station-city-route --device.version=v2.0.0 --device.route=tunis-lac2-station-city-route"
mvn org.springframework.boot:spring-boot-maven-plugin:run -Dspring-boot.run.arguments="--server.port=7002 --cert.name=tunis-lac2-station-beach-route --device.version=v2.0.0 --device.route=tunis-lac2-station-beach-route"
```