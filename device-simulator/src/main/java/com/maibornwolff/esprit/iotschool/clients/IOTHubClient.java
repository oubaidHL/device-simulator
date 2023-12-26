package com.maibornwolff.esprit.iotschool.clients;

import com.maibornwolff.esprit.iotschool.callbacks.DesiredPropertiesChangedCallbackImpl;
import com.maibornwolff.esprit.iotschool.callbacks.DirectMethodCallback;
import com.maibornwolff.esprit.iotschool.callbacks.IotHubConnectionStatusChangeCallbackLogger;
import com.maibornwolff.esprit.iotschool.methods.Method;
import com.maibornwolff.esprit.iotschool.status.DeviceProvisioningStatus;
import com.maibornwolff.esprit.iotschool.tasks.Task;
import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode;
import com.microsoft.azure.sdk.iot.device.exceptions.IotHubClientException;
import com.microsoft.azure.sdk.iot.device.twin.ReportedPropertiesUpdateResponse;
import com.microsoft.azure.sdk.iot.device.twin.Twin;
import com.microsoft.azure.sdk.iot.device.twin.TwinCollection;
import com.microsoft.azure.sdk.iot.provisioning.security.SecurityProvider;

import java.io.IOException;
import java.util.List;

public class IOTHubClient {

    /**
     * Mqtt client connected to the iot hub
     */
    private DeviceClient deviceClient;

    /**
     * Store the status of the device twin
     */
    private Twin twin;

    /**
     * Connect the client to the IOT hub using the result from the DPS
     *
     * @param deviceProvisioningStatus dps status, containing url of the iot hub to connect
     * @param securityProviderX509     certificates - pem chain and private key for authentication
     * @return true if connected successfully
     */
    public boolean connect(final DeviceProvisioningStatus deviceProvisioningStatus, final SecurityProvider securityProviderX509) {
        // connect to iot hub
        String iotHubUri = deviceProvisioningStatus.provisioningDeviceClientRegistrationInfoClient.getIothubUri();
        String deviceId = deviceProvisioningStatus.provisioningDeviceClientRegistrationInfoClient.getDeviceId();

        // connected to DPS, got IOT Hub parameters
        System.out.println("## DPS Status: IotHub Uri: " + deviceProvisioningStatus.provisioningDeviceClientRegistrationInfoClient.getIothubUri());
        System.out.println("## DPS Status: Device ID: " + deviceProvisioningStatus.provisioningDeviceClientRegistrationInfoClient.getDeviceId());

        System.out.println("## Opening connection to IoT hub");
        try {
            deviceClient = new DeviceClient(iotHubUri, deviceId, securityProviderX509, IotHubClientProtocol.MQTT);

            deviceClient.setConnectionStatusChangeCallback(new IotHubConnectionStatusChangeCallbackLogger(), new Object());
            deviceClient.open(true);

            System.out.println("Subscribing to desired properties");
            deviceClient.subscribeToDesiredProperties(new DesiredPropertiesChangedCallbackImpl(() -> twin), null);

            System.out.println("Getting current twin");
            twin = deviceClient.getTwin();
            System.out.println("Received current twin:");
            System.out.println(twin);
        } catch (IOException e) {
            System.err.println("> Device client threw an exception: " + e.getMessage());
            e.printStackTrace();
            close();
            return false;
        } catch (IotHubClientException e) {
            System.err.println("> Failed to connect to iot hub");
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            System.err.println("> Failed to subscribe to device twin");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void subscribeToMethods(final List<Method> methods) throws IotHubClientException, InterruptedException {
        System.out.println("Subscribing to methods");
        deviceClient.subscribeToMethods(new DirectMethodCallback(methods, () -> twin), null);
    }

    /**
     * Main loop of the simulated device
     * Will send messages to the iot hub with its current status
     */
    public void loop(final List<Task> tasks) {
        while (true) {

            for (final Task t : tasks) {
                t.run(twin);
            }

            // send update and wait for next loop
            sendUpdate();
        }
    }

    private void sendUpdate() {
        TwinCollection reportedProperties = twin.getReportedProperties();
        ReportedPropertiesUpdateResponse response = null;
        try {
            // After getting the current twin, you can begin sending reported property updates. You can send reported
            // property updates without getting the current twin as long as you have the correct reported properties
            // version. If you send reported properties and receive a "precondition failed" error, then your reported
            // properties version is out of date. Get the latest version by calling getTwin() again.
            response = deviceClient.updateReportedProperties(reportedProperties);

            // After a successful update of the device's reported properties, the service will provide the new
            // reported properties version for the twin. You'll need to save this value in your twin object's reported
            // properties object so that subsequent updates don't fail with a "precondition failed" error.
            twin.getReportedProperties().setVersion(response.getVersion());
        } catch (InterruptedException ignored) {
            System.err.println("> Failed to send updated properties to the iot hub. Will retry next time");
        } catch (IotHubClientException e) {
            System.err.println("> Failed to send updated properties to the iot hub. Will retry next time");
            if (e.getStatusCode() == IotHubStatusCode.PRECONDITION_FAILED) {
                try {
                    twin = deviceClient.getTwin();
                } catch (InterruptedException | IotHubClientException ex) {
                    System.err.println("> Failed to refresh the device twin from the iot hub");
                }
            }
        } finally {
            // wait one second
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void close() {
        if (deviceClient != null) {
            deviceClient.close();
        }
    }
}
