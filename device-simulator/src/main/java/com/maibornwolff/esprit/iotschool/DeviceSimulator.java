package com.maibornwolff.esprit.iotschool;

import com.microsoft.azure.sdk.iot.device.exceptions.IotHubClientException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This is the program entrypoint
 * It starts a spring boot application server, that will listen onto port 5000 (configured in `application.properties`)
 */
@SpringBootApplication
public class DeviceSimulator {

    public static void main(String[] args) throws IotHubClientException, InterruptedException {
        SpringApplication.run(DeviceSimulator.class, args);

        new IotDevice().run(args);
    }

}
