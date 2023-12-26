package com.maibornwolff.esprit.iotschool.tasks;

import com.maibornwolff.esprit.iotschool.IotDevice;
import com.microsoft.azure.sdk.iot.device.twin.Twin;
import com.microsoft.azure.sdk.iot.device.twin.TwinCollection;

import java.util.Random;


/**
 * Task that changes the temperature based on the current and desired temperature
 * initialized in {@link IotDevice#createTasks()}
 */
public class TemperatureTask implements Task {

    private final double DEFAULT_DESIRED_TEMPERATURE = 19.5;
    private final double STARTING_REPORTED_TEMPERATURE = 21.0;

    private final static String TEMPERATURE_KEY = "temperature";
    private final static String AIR_CONDITIONING_KEY = "airConditioning";
    private final static String HEATING_KEY = "heating";

    private final Random r = new Random();

    @Override
    public void run(Twin deviceTwin) {
        final TwinCollection reportedProperties = deviceTwin.getReportedProperties();
        Object reportedTemperatureObject = reportedProperties.get(TEMPERATURE_KEY);

        // start with some temperature
        if (!(reportedTemperatureObject instanceof Number)) {
            reportedTemperatureObject = STARTING_REPORTED_TEMPERATURE;
            reportedProperties.put(TEMPERATURE_KEY, reportedTemperatureObject);
        }

        Object desiredTemperatureObject;
        // TODO exercise 2: Read the desired temperature from the device twin
        // TODO exercise 2: Perform null and type checks and use DEFAULT_DESIRED_TEMPERATURE as a default if none is set

        // now those are not null
        final double reportedTemperature = ((Number) reportedTemperatureObject).doubleValue();
        final double desiredTemperature;

        // TODO exercise 2: Update the reported temperature to get closer to the desired one, simulating an A/C or heating process
        // TODO exercise 2: The temperature should be a string with 2 digit precision
        // TODO exercise 2: Also update the reported values for HEATING_KEY and AIR_CONDITIONING_KEY
        // TODO exercise 2: both of those values are boolean flags indicating if the heater or A/C is currently running

        // TODO exercise 2: A formula for changing the temperature could be
        // TODO exercise 2: - get the current gap between desired and reported temp
        // TODO exercise 2: - if they differ by less than 1 degree, turn heating and a/c off and do nothing
        // TODO exercise 2: - if they differ by more than 1 degree, turn heating or a/c on
        // TODO exercise 2:   and change the new reported temperature to be 0.1 degree closer to the desired than before

        // TODO exercise 2: calculation here
        boolean airConditioning = false;
        boolean heating = false;
        String updatedTemperature = "TODO exercise 2: NOT YET IMPLEMENTED";

        reportedProperties.put(AIR_CONDITIONING_KEY, airConditioning);
        reportedProperties.put(HEATING_KEY, heating);


        reportedProperties.put(TEMPERATURE_KEY, updatedTemperature);
    }
}
