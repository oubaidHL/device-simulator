package com.maibornwolff.esprit.backendapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceMethod;
import com.microsoft.azure.sdk.iot.service.devicetwin.MethodResult;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.maibornwolff.esprit.backendapp.config.StaticHubConfig.IOT_HUB_CONNECTION_STRING;

@RestController
public class DeviceDirectMethodController {


    private static final Long responseTimeout = TimeUnit.SECONDS.toSeconds(30);
    private static final Long connectTimeout = TimeUnit.SECONDS.toSeconds(5);


    /**
     * Invoke a direct device method for the given device with given payload
     * curl -X POST -H "Accept: application/json" -H 'Content-Type: application/json' "localhost:8085/backend-app/device-method/airport-route?methodName=reboot" | jq
     *
     * @param deviceId   id of the device, must be connected
     * @param methodName name of the method to invoke in the device
     * @param payload    payload to send to the device. Needs to be valid json, or single value like quoted string, or integer
     * @return the response from the device
     */
    @PostMapping("/backend-app/device-method/{deviceId}")
    public Object invokeDeviceMethod(
            @PathVariable(name = "deviceId") String deviceId,
            @RequestParam(name = "methodName") String methodName,
            @RequestBody(required = false) Object payload
    ) {

        // instantiate client
        final DeviceMethod methodClient;
        try {
            methodClient = DeviceMethod.createFromConnectionString(IOT_HUB_CONNECTION_STRING);
        } catch (IOException e) {
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, e);
        }

        try {
            // call method on the device
            // this will fail if the device is offline
            MethodResult result = methodClient.invoke(deviceId, methodName, responseTimeout, connectTimeout, payload);

            System.out.printf("- Invoking '%s' on device '%s'%s\n",
                    methodName,
                    deviceId,
                    payload != null ? ": " + new ObjectMapper().writeValueAsString(payload) : "");


            if (result == null) {
                throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, ProblemDetail.forStatusAndDetail(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "response from the device is null"
                ), null);
            }

            System.out.println("- Status for device:   " + result.getStatus());
            System.out.println("- Message from device: " + result.getPayload());

            return result.getPayload();
        } catch (IotHubException | JsonProcessingException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();

            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, e);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();

            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }
}
