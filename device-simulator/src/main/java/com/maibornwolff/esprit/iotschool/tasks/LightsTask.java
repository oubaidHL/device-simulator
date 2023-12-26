package com.maibornwolff.esprit.iotschool.tasks;

import com.maibornwolff.esprit.iotschool.IotDevice;
import com.microsoft.azure.sdk.iot.device.twin.Twin;


/**
 * Task that turns the lights on or off
 * initialized in {@link IotDevice#createTasks()}
 */
public class LightsTask implements Task {

    private final static String LIGHTS_KEY = "lights";

    @Override
    public void run(Twin deviceTwin) {
        // read the desired and current value of the "lights" status from the device twin
        final Object desiredLightsObject = deviceTwin.getDesiredProperties().get(LIGHTS_KEY);
        final Object currentLightsObject = deviceTwin.getReportedProperties().get(LIGHTS_KEY);

        // both should be boolean, but could be null (if not yet set), so cast to boolean if it is a boolean, otherwise assume false
        final boolean desiredLights = (desiredLightsObject != null && desiredLightsObject instanceof Boolean) ? (boolean) desiredLightsObject : false;
        final boolean currentLights = (currentLightsObject != null && currentLightsObject instanceof Boolean) ? (boolean) currentLightsObject : false;

        // check if the lights should change
        if (desiredLights && !currentLights) {
            System.out.println("- Turning the lights on");
        }
        if (!desiredLights && currentLights) {
            System.out.println("- Turning the lights off");
        }

        // set the lights to the desired state
        deviceTwin.getReportedProperties().put(LIGHTS_KEY, desiredLights);
    }
}
