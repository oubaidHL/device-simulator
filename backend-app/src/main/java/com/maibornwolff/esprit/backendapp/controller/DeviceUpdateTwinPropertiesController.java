package com.maibornwolff.esprit.backendapp.controller;

import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwin;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwinDevice;
import com.microsoft.azure.sdk.iot.service.devicetwin.Pair;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.maibornwolff.esprit.backendapp.config.StaticHubConfig.IOT_HUB_CONNECTION_STRING;

@RestController
public class DeviceUpdateTwinPropertiesController {

    final static private String TEMPERATURE_KEY = "temperature";
    final static private String LIGHTS_KEY = "lights";

    /**
     * Sets the given parameters as "properties.desired" in the device twin
     * curl -X POST -H "Accept: application/json" -H 'Content-Type: application/json' "localhost:8085/backend-app/device-properties/airport-route?temperature=25.9&lights=true" | jq
     *
     * @param deviceId    identifier of the device
     * @param temperature new target temperate
     * @param lights      new lights status (true for on, false for off)
     * @return updated properties from the device
     */
    @PostMapping("/backend-app/device-properties/{deviceId}")
    public Object updateDeviceTwinProperties(
            @PathVariable(name = "deviceId") String deviceId,
            @RequestParam(name = "temperature", defaultValue = "") Double temperature,
            @RequestParam(name = "lights", defaultValue = "") Boolean lights
    ) {

        // Get the DeviceTwin and DeviceTwinDevice objects
        // instantiate client
        final DeviceTwin twinClient;
        try {
            twinClient = DeviceTwin.createFromConnectionString(IOT_HUB_CONNECTION_STRING);
        } catch (IOException e) {
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, e);
        }

        // init twinDevice twin (does not connect to the hub yet)
        final DeviceTwinDevice twinDevice = new DeviceTwinDevice(deviceId);

        try {
            // Get the twinDevice twin from IoT Hub
            System.out.printf("""
                            - Device twin for %s before update:
                            reported: %s
                            desired: %s
                            """,
                    deviceId,
                    twinDevice.getReportedProperties().stream().collect(
                            Collectors.toMap(
                                    Pair::getKey,
                                    Pair::getValue)
                    ),
                    twinDevice.getDesiredProperties().stream().collect(
                            Collectors.toMap(
                                    Pair::getKey,
                                    Pair::getValue)
                    )
            );

            // Create the tags and attach them to the DeviceTwinDevice object
            Set<Pair> properties = new HashSet<>();

            if (temperature != null) {
                System.out.printf("""
                                - Updating desired property for '%s'
                                '%s' to '%s'
                                """,
                        deviceId,
                        TEMPERATURE_KEY, temperature
                );
                properties.add(new Pair(TEMPERATURE_KEY, temperature));
            }
            if (lights != null) {
                System.out.printf("""
                                - Updating desired property for '%s'
                                '%s' to '%s'
                                """,
                        deviceId,
                        LIGHTS_KEY, lights
                );
                properties.add(new Pair(LIGHTS_KEY, lights));
            }

            twinDevice.setDesiredProperties(properties);
            System.out.println("- Successfully set desired properties");

            // Update the twinDevice twin in IoT Hub
            System.out.println("- Updating twinDevice twin");
            twinClient.updateTwin(twinDevice);

            // refresh the status
            twinClient.getTwin(twinDevice);

            return twinDevice.getReportedProperties().stream().collect(
                    Collectors.toMap(
                            Pair::getKey,
                            Pair::getValue)
            );
        } catch (IotHubException | IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();

            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to update the twinDevice"
            ), e);
        }
    }
}
