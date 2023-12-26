package com.maibornwolff.esprit.backendapp.controller;

import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwin;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwinDevice;
import com.microsoft.azure.sdk.iot.service.devicetwin.Query;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.maibornwolff.esprit.backendapp.config.StaticHubConfig.IOT_HUB_CONNECTION_STRING;

@RestController
public class DeviceFilterTagsController {
    @RequestMapping("/backend-app/device-filter/tags")
    public List<String> findDevices(
            @RequestParam(name = "region", required = false) String region,
            @RequestParam(name = "plant", required = false) String plant,
            @RequestParam(name = "connectivityType", required = false) String connectivityType,
            @RequestParam(name = "driverName", required = false) String driverName,
            @RequestParam(name = "manufacturer", required = false) String manufacturer,
            @RequestParam(name = "manufacturingDate", required = false) String manufacturingDate
    ) throws IOException {
        // Get the DeviceTwin object
        DeviceTwin twinClient = DeviceTwin.createFromConnectionString(IOT_HUB_CONNECTION_STRING);

        try {
            // Query the device twins in IoT Hub
            System.out.println("Query Devices in IoT Hub:");
            Map<String, String> queryFilters = new HashMap<>() {{
                put("region", region);
                put("plant", plant);
                put("connectivityType", connectivityType);
                put("driverName", driverName);
                put("manufacturer", manufacturer);
                put("manufacturingDate", manufacturingDate);
            }};

            StringBuilder sbQuery = new StringBuilder();
            sbQuery.append("select * from devices where (connectionState='Disconnected' or connectionState='Connected')");
            for (var filter : queryFilters.entrySet()) {
                if (!(filter.getValue() == null) && !filter.getValue().equals("") && !filter.getValue().trim().equals(""))
                    sbQuery.append(" and tags." + filter.getKey() + "='" + filter.getValue() + "'");
            }
            System.out.println(sbQuery);

            final List<String> devices = new ArrayList<>();

            // Run the query, returning a maximum of 100 devices
            Query twinQuery = twinClient.queryTwin(sbQuery.toString());
            while (twinClient.hasNextDeviceTwin(twinQuery)) {
                DeviceTwinDevice deviceTwin = twinClient.getNextDeviceTwin(twinQuery);
                twinClient.getTwin(deviceTwin);

                // grab them here then check on temp from rest query
                final String deviceDescription = String.format(
                        "%s: %s",
                        deviceTwin.getDeviceId(),
                        deviceTwin.getConnectionState()
                );
                System.out.println("- " + deviceDescription);
                devices.add(deviceDescription);
            }

            return devices;
        } catch (IotHubException | IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();

            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "!! Failed to request devices from hub !!"
            ), e);
        }
    }
}
