package com.maibornwolff.esprit.iotschool;

import com.maibornwolff.esprit.iotschool.certs.SecurityProviderBuilder;
import com.maibornwolff.esprit.iotschool.clients.DeviceProvisioningClient;
import com.maibornwolff.esprit.iotschool.clients.IOTHubClient;
import com.maibornwolff.esprit.iotschool.methods.AnnounceMethod;
import com.maibornwolff.esprit.iotschool.methods.Method;
import com.maibornwolff.esprit.iotschool.methods.RebootMethod;
import com.maibornwolff.esprit.iotschool.status.DeviceProvisioningStatus;
import com.maibornwolff.esprit.iotschool.tasks.*;
import com.microsoft.azure.sdk.iot.device.exceptions.IotHubClientException;
import com.microsoft.azure.sdk.iot.provisioning.device.ProvisioningDeviceClientStatus;
import com.microsoft.azure.sdk.iot.provisioning.security.SecurityProvider;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

public class IotDevice implements CommandLineRunner {

    private String certificateName = "device";

    private String deviceVersion = "v1.0.5";

    private String deviceRoute = "tourist-route";

    @Override
    public void run(String... args) throws IotHubClientException, InterruptedException {
        System.out.println("# Starting device client...");
        // build certificates
        SecurityProvider securityProviderX509 = new SecurityProviderBuilder().build(certificateName);
        if (securityProviderX509 == null) {
            System.err.println("> Failed to read certificates");
            return;
        }

        // connect to DPS
        DeviceProvisioningStatus deviceProvisioningStatus = new DeviceProvisioningClient().connect(securityProviderX509);
        // catch failures connecting to DPS
        if (deviceProvisioningStatus.provisioningDeviceClientRegistrationInfoClient.getProvisioningDeviceClientStatus() != ProvisioningDeviceClientStatus.PROVISIONING_DEVICE_STATUS_ASSIGNED) {
            System.err.println("> Failed to connect to DPS");
            return;
        }

        // connect to IOT hub
        IOTHubClient iotHubClient = new IOTHubClient();
        boolean connected = iotHubClient.connect(deviceProvisioningStatus, securityProviderX509);
        if (!connected) {
            System.err.println("> Failed to connect to IotHub");
            return;
        }

        iotHubClient.subscribeToMethods(createMethods());

        System.out.println("## Running ##");

        // run main loop
        // listen for messages from the hub and send actual device state
        iotHubClient.loop(createTasks());

        // make sure to disconnect on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("## Powering down simulated device ##");
            iotHubClient.close();
        }));
    }


    /**
     * Iot School Exercise: This creates the methods that the backend app can call
     */
    private List<Method> createMethods() {
        return List.of(
                new RebootMethod(), // already implemented
                new AnnounceMethod() // contains TODOs: TODO exercise 3: ...
        );
    }

    /**
     * Iot School Exercise: This defines all the things the simulated device will do during its connection
     * Look at the already implemented ones for examples and add the ones with TODOs in them
     */
    private List<Task> createTasks() {
        return List.of(
                new ConfigTask(deviceVersion, deviceRoute), // already implemented
                new TimeTask(), // already implemented
                new LightsTask(), // already implemented
                new TemperatureTask(), // contains TODOs: TODO exercise 2: ...
                new RebootTask(), // already implemented
                new PositionTask( // contains TODOs: TODO exercise 1: ...
                        List.of(
                                Pair.of(0.0, 0.0),
                                Pair.of(0.5, 0.0),
                                Pair.of(0.5, 1.0),
                                Pair.of(1.5, 2.0),
                                Pair.of(3.0, 2.5),
                                Pair.of(3.5, 2.8),
                                Pair.of(4.5, 2.9),
                                Pair.of(7.0, 3.0),
                                Pair.of(9.0, 3.0),
                                Pair.of(10.0, 3.0),
                                Pair.of(10.1, 2.5),
                                Pair.of(10.2, 2.5),
                                Pair.of(10.1, 2.5),
                                Pair.of(9.5, 2.5),
                                Pair.of(8.0, 2.4),
                                Pair.of(8.0, 2.3),
                                Pair.of(6.0, 2.0),
                                Pair.of(4.0, 1.5),
                                Pair.of(2.0, 1.0),
                                Pair.of(1.0, 1.0),
                                Pair.of(0.5, 0.5),
                                Pair.of(0.2, 0.2),
                                Pair.of(0.1, 0.1),
                                Pair.of(0.0, 0.0),
                                Pair.of(0.0, 0.0),
                                Pair.of(0.0, 0.0),
                                Pair.of(0.0, 0.0)
                        )
                )
        );
    }
}