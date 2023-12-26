package com.maibornwolff.esprit.backendapp.controller;

import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwin;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwinDevice;
import com.microsoft.azure.sdk.iot.service.devicetwin.Query;
import com.microsoft.azure.sdk.iot.service.devicetwin.SqlQuery;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.maibornwolff.esprit.backendapp.config.StaticHubConfig.IOT_HUB_CONNECTION_STRING;

@RestController
public class DeviceFilterTemperatureController {
    @RequestMapping("/backend-app/device-filter/temperature")
    public List<String> findDevices(@RequestParam(name = "hotter") int hotter) throws IOException {
        // Get the DeviceTwin object
        DeviceTwin twinClient = DeviceTwin.createFromConnectionString(IOT_HUB_CONNECTION_STRING);

        try {
            // Query the device twins in IoT Hub
            System.out.println("Device Query:");

            // Construct the query
            SqlQuery sqlQuery = SqlQuery.createSqlQuery("*", SqlQuery.FromType.DEVICES, null, null);


            final List<String> devices = new ArrayList<>();

            // Run the query, returning a maximum of 100 devices
            Query twinQuery = twinClient.queryTwin(sqlQuery.getQuery(), 100);
            while (twinClient.hasNextDeviceTwin(twinQuery)) {
                DeviceTwinDevice deviceTwin = twinClient.getNextDeviceTwin(twinQuery);
                twinClient.getTwin(deviceTwin);

                // TODO exercise 5: get the reported properties from the device twin
                // TODO exercise 5: get the value of the "temperature" property
                // TODO exercise 5: try interpreting it as a number
                // TODO exercise 5: then check if the "hotter" input parameter is > the "temperature"
                if (true) {
                    // TODO exercise 5: create a description of the device, including
                    // TODO exercise 5: the device id / device name and current connection status and current temperature
                    // TODO exercise 5: then add the string to the "devices" list and log it
                } else {
                    // TODO exercise 5: the device id / device name and current connection status and current temperature
                    // TODO exercise 5: as not matching but don't add it to the list
                }
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
