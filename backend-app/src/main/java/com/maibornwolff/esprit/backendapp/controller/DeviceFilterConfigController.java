package com.maibornwolff.esprit.backendapp.controller;

import com.microsoft.azure.sdk.iot.service.devicetwin.*;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.maibornwolff.esprit.backendapp.config.StaticHubConfig.IOT_HUB_CONNECTION_STRING;

@RestController
public class DeviceFilterConfigController {

    /**
     * Define a GET method to fetch all devices matching the given filter criteria
     * curl -H "Accept: application/json" "localhost:8085/backend-app/device-config?version=v1.0.5" | jq
     *
     * @param version might be null, to accept all version
     * @param route   might be null, to accept all routes
     * @return a list of deviceId - connectionStatus - deviceStatus
     */
    @RequestMapping("/backend-app/device-filter/config")
    public List<String> findDevices(
            @RequestParam(name = "version", defaultValue = "") String version,
            @RequestParam(name = "route", defaultValue = "") String route
    ) {

        // instantiate client
        final DeviceTwin twinClient;
        final SqlQuery sqlQuery;
        try {
            twinClient = DeviceTwin.createFromConnectionString(IOT_HUB_CONNECTION_STRING);
            sqlQuery = findDevicesBy(version, route);
        } catch (IOException e) {
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, e);
        }


        try {
            // Run the query, returning a maximum of 100 devices
            final Query twinQuery = twinClient.queryTwin(sqlQuery.getQuery(), 100);

            final List<String> devices = new ArrayList<>();

            while (twinClient.hasNextDeviceTwin(twinQuery)) {
                // the query api will return a cached state of the device twin
                // that might be up to 5 minutes old
                final DeviceTwinDevice deviceTwin = twinClient.getNextDeviceTwin(twinQuery);

                // actually load the twin, including connection state and up-to-date properties
                twinClient.getTwin(deviceTwin);
                final Map<String, Object> reportedProperties = deviceTwin.getReportedProperties().stream().collect(
                        Collectors.toMap(
                                Pair::getKey,
                                Pair::getValue)
                );

                // grab them here then check on temp from rest query
                // TODO exercise 4: build a nice description string from the device
                // TODO exercise 4: it should include the device name / device id, as well as the current status (connected or notConnected)
                final String deviceDescription = "TODO exercise 4: build a dynamic query including the version and route";

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

    /**
     * Build a query to find device twins based on their version and route from the _config
     *
     * @param version might be null, to accept all version
     * @param route   might be null, to accept all routes
     * @return query
     * @throws IOException in case the query if invalid
     */
    private SqlQuery findDevicesBy(final String version, final String route) throws IOException {

        // TODO exercise 4: build a dynamic query including the version and route
        // TODO exercise 4: the query should not check for the version if it is null
        // TODO exercise 4: the query should not check for the route if it is null
        // TODO exercise 4: so if called with null, null this should build a query that matches all devices
        final String queryString = "properties.reported.status='TODO'";

        return SqlQuery.createSqlQuery(
                "*",
                SqlQuery.FromType.DEVICES,
                queryString,
                null
        );
    }
}
