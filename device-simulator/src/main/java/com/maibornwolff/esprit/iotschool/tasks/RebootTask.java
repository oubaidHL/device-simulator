package com.maibornwolff.esprit.iotschool.tasks;

import com.maibornwolff.esprit.iotschool.IotDevice;
import com.maibornwolff.esprit.iotschool.methods.RebootMethod;
import com.microsoft.azure.sdk.iot.device.twin.Twin;
import com.microsoft.azure.sdk.iot.device.twin.TwinCollection;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

/**
 * Task that checks the device status (running or rebooting) and sets its reported values accordingly
 * initialized in {@link IotDevice#createTasks()}
 */
public class RebootTask implements Task {

    private final static String STATUS_KEY = "status";
    public final static String REBOOT_COMPLETE_TIMESTAMP_KEY = "rebootCompleteTimestamp";
    private final static long REBOOT_TIME_MS = 5_000;

    public RebootTask() {
    }

    @Override
    public void run(Twin deviceTwin) {
        TwinCollection reportedProperties = deviceTwin.getReportedProperties();
        final Object rebootStartObject = reportedProperties.get(RebootMethod.REBOOT_START_TIMESTAMP_KEY);
        if (!(rebootStartObject instanceof String)) { // this is true if the value is null
            // no restart happening
            reportedProperties.put(STATUS_KEY, "running");
            return;
        }

        final OffsetDateTime rebootStart;
        try {
            rebootStart = OffsetDateTime.parse((String) rebootStartObject);
        } catch (DateTimeParseException ignored) {
            System.err.println("Failed to read reboot start time from '" + rebootStartObject + "'");
            reportedProperties.put(STATUS_KEY, "running");
            return;
        }
        final OffsetDateTime now = OffsetDateTime.now();

        if (!(now.toInstant().toEpochMilli() - rebootStart.toInstant().toEpochMilli() > REBOOT_TIME_MS)) {
            // restart not yet complete
            reportedProperties.put(STATUS_KEY, "restarting");
            return;
        }

        // reboot complete, set last reboot timestamp and clear reboot start timestamp
        System.out.println("!! Reboot complete at " + now + " !!");
        reportedProperties.put(STATUS_KEY, "running");
        reportedProperties.put(REBOOT_COMPLETE_TIMESTAMP_KEY, now.toString());
        reportedProperties.put(RebootMethod.REBOOT_START_TIMESTAMP_KEY, null);
    }
}
