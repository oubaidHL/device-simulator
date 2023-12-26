package com.maibornwolff.esprit.iotschool.methods;

import com.microsoft.azure.sdk.iot.device.twin.DirectMethodPayload;
import com.microsoft.azure.sdk.iot.device.twin.DirectMethodResponse;
import com.microsoft.azure.sdk.iot.device.twin.Twin;

import java.time.OffsetDateTime;

/**
 * This will react to the "reboot" method call
 * It then sets the device twin state to "rebooting" which is handled by the {@link com.maibornwolff.esprit.iotschool.tasks.RebootTask}
 */
public class RebootMethod implements Method {

    public final static String REBOOT_START_TIMESTAMP_KEY = "rebootStartTimestamp";

    @Override
    public String getName() {
        return "reboot";
    }

    @Override
    public DirectMethodResponse run(Twin deviceTwin, DirectMethodPayload payload) {
        String rebootTime = OffsetDateTime.now().toString();
        System.out.println("!! Rebooting device at " + rebootTime + " !!");
        deviceTwin.getReportedProperties().put(REBOOT_START_TIMESTAMP_KEY, rebootTime);
        return new DirectMethodResponse(200, "Rebooting...");
    }
}
