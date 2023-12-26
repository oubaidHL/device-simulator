package com.maibornwolff.esprit.backendapp.controller;

import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwin;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwinDevice;
import com.microsoft.azure.sdk.iot.service.devicetwin.Pair;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static com.maibornwolff.esprit.backendapp.config.StaticHubConfig.IOT_HUB_CONNECTION_STRING;

@RestController
public class DeviceTwinTagsController {

    @RequestMapping("/backend-app/device-tags/{deviceId}")
    public String runDeviceTwinTags(
            @PathVariable(name = "deviceId") String deviceId,
            @RequestParam(name = "region") String region,
            @RequestParam(name = "plant") String plant,
            @RequestParam(name = "connectivityType") String connectivityType,
            @RequestParam(name = "driverName") String driverName,
            @RequestParam(name = "manufacturer") String manufacturer,
            @RequestParam(name = "manufacturingDate") String manufacturingDate) throws IOException {


        // Get the DeviceTwin and DeviceTwinDevice objects
        DeviceTwin twinClient = DeviceTwin.createFromConnectionString(IOT_HUB_CONNECTION_STRING);
        DeviceTwinDevice device = new DeviceTwinDevice(deviceId);

        try {
            // Get the device twin from IoT Hub
            System.out.println("Device twin before update:");
            twinClient.getTwin(device);
            System.out.println(device);

            // Create the tags and attach them to the DeviceTwinDevice object
            Set<Pair> tags = new HashSet<Pair>();
            tags.add(new Pair("region", region));
            tags.add(new Pair("plant", plant));
            tags.add(new Pair("connectivityType", connectivityType));
            tags.add(new Pair("driverName", driverName));
            tags.add(new Pair("manufacturer", manufacturer));
            tags.add(new Pair("manufacturingDate", manufacturingDate));
            device.setTags(tags);

            // Update the device twin in IoT Hub
            System.out.println("Updating device twin");
            twinClient.updateTwin(device);

            // Retrieve the device twin with the tag values from IoT Hub
            System.out.println("Device twin after update:");
            twinClient.getTwin(device);
            System.out.println(device);
        } catch (IotHubException | IOException e) {
            System.out.println(e.getMessage());
        }

        return "Tags for IoT device with deviceId=\"" + deviceId + "\" has been set.";
    }
}
