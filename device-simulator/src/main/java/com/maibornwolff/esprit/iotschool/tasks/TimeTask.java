package com.maibornwolff.esprit.iotschool.tasks;

import com.maibornwolff.esprit.iotschool.IotDevice;
import com.microsoft.azure.sdk.iot.device.twin.Twin;

import java.time.OffsetDateTime;


/**
 * Task that reports the current device time
 * initialized in {@link IotDevice#createTasks()}
 */
public class TimeTask implements Task {

    private final static String TIME_KEY = "time";

    @Override
    public void run(Twin deviceTwin) {
        deviceTwin.getReportedProperties().put(TIME_KEY, OffsetDateTime.now().toString());
    }
}
