package com.maibornwolff.esprit.iotschool.clients;

import com.maibornwolff.esprit.iotschool.callbacks.DeviceProvisioningDeviceClientRegistrationCallbackImpl;
import com.maibornwolff.esprit.iotschool.config.StaticDeviceConfig;
import com.maibornwolff.esprit.iotschool.status.DeviceProvisioningStatus;
import com.microsoft.azure.sdk.iot.provisioning.device.ProvisioningDeviceClient;
import com.microsoft.azure.sdk.iot.provisioning.device.ProvisioningDeviceClientStatus;
import com.microsoft.azure.sdk.iot.provisioning.device.internal.exceptions.ProvisioningDeviceClientException;
import com.microsoft.azure.sdk.iot.provisioning.security.SecurityProvider;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeoutException;

public class DeviceProvisioningClient {

    private ProvisioningDeviceClient provisioningDeviceClient;

    /**
     * Try connecting to the dps which then returns the connection parameters of the iot hub
     *
     * @param securityProviderX509 cert chain and private key
     * @return status of the connection
     */
    public DeviceProvisioningStatus connect(final SecurityProvider securityProviderX509) {

        // final status
        DeviceProvisioningStatus deviceProvisioningStatus = new DeviceProvisioningStatus();

        try {

            // create client
            provisioningDeviceClient = ProvisioningDeviceClient.create(
                    StaticDeviceConfig.GLOBAL_ENDPOINT,
                    StaticDeviceConfig.ID_SCOPE,
                    StaticDeviceConfig.TRANSPORT_PROTOCOL,
                    securityProviderX509);

            // try registering a device
            provisioningDeviceClient.registerDevice(new DeviceProvisioningDeviceClientRegistrationCallbackImpl(), deviceProvisioningStatus);

            final long start = OffsetDateTime.now().toInstant().toEpochMilli();
            // with retries
            while (deviceProvisioningStatus.provisioningDeviceClientRegistrationInfoClient.getProvisioningDeviceClientStatus() != ProvisioningDeviceClientStatus.PROVISIONING_DEVICE_STATUS_ASSIGNED) {

                if (OffsetDateTime.now().toInstant().toEpochMilli() - start > StaticDeviceConfig.MAX_TIME_TO_WAIT_FOR_REGISTRATION_MS) {
                    deviceProvisioningStatus.exception = new TimeoutException("Failed to connect to DPS for " + StaticDeviceConfig.MAX_TIME_TO_WAIT_FOR_REGISTRATION_MS + "ms");
                    System.err.println("> Failed to connect to DPS for " + StaticDeviceConfig.MAX_TIME_TO_WAIT_FOR_REGISTRATION_MS + "ms");
                    close();
                    return deviceProvisioningStatus;
                }

                if (deviceProvisioningStatus.provisioningDeviceClientRegistrationInfoClient.getProvisioningDeviceClientStatus() == ProvisioningDeviceClientStatus.PROVISIONING_DEVICE_STATUS_ERROR ||
                        deviceProvisioningStatus.provisioningDeviceClientRegistrationInfoClient.getProvisioningDeviceClientStatus() == ProvisioningDeviceClientStatus.PROVISIONING_DEVICE_STATUS_DISABLED ||
                        deviceProvisioningStatus.provisioningDeviceClientRegistrationInfoClient.getProvisioningDeviceClientStatus() == ProvisioningDeviceClientStatus.PROVISIONING_DEVICE_STATUS_FAILED) {
                    deviceProvisioningStatus.exception.printStackTrace();
                    System.err.println("> Registration error, bailing out");
                    close();
                    return deviceProvisioningStatus;
                }
                System.err.println("> Waiting for Provisioning Service to register");
                // wait for one second
                Thread.sleep(1_000);
            }
        } catch (ProvisioningDeviceClientException e) {
            System.err.println("> Failed to connect to the DPS. Check the content of the DPSConfig class.");
            e.printStackTrace();
            deviceProvisioningStatus.exception = e;
        } catch (InterruptedException e) {
            System.err.println("> Failed to wait before retrying connection.");
            e.printStackTrace();
            deviceProvisioningStatus.exception = e;
        }

        close();
        return deviceProvisioningStatus;
    }

    private void close() {
        if (provisioningDeviceClient != null) {
            provisioningDeviceClient.close();
        }
    }
}
