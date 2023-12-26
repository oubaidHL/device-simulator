package com.maibornwolff.esprit.iotschool.tasks;

import com.maibornwolff.esprit.iotschool.IotDevice;
import com.microsoft.azure.sdk.iot.device.twin.Twin;

import java.util.Map;

/**
 * Task that reports a static config for this device
 * initialized in {@link IotDevice#createTasks()}
 */
public class ConfigTask implements Task {

    private final static String CONFIG_KEY = "_config";
    private final static String VERSION_KEY = "version";
    private final static String ROUTE_KEY = "route";

    private final String version;
    private final String route;

    public ConfigTask(String version, String route) {
        this.version = version;
        this.route = route;
    }

    @Override
    public void run(Twin deviceTwin) {
        deviceTwin.getReportedProperties().put(CONFIG_KEY,
                Map.of(
                        VERSION_KEY, version,
                        ROUTE_KEY, route
                )
        );
    }
}
