package com.maibornwolff.esprit.iotschool.tasks;

import com.maibornwolff.esprit.iotschool.IotDevice;
import com.microsoft.azure.sdk.iot.device.twin.Twin;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Task to report the current position of the train
 * initialized in {@link IotDevice#createTasks()}
 */
public class PositionTask implements Task {

    private final static String POSITION_KEY = "position";

    private final List<Pair<Double, Double>> route;
    private int pos = 0;

    public PositionTask(List<Pair<Double, Double>> route) {
        this.route = route;
    }

    @Override
    public void run(Twin deviceTwin) {
        // TODO exercise 1: Update the reported property for POSITION_KEY to the current position of the train
        // TODO exercise 1: The position should be sent as a string containing x and y coordinate with 2 digits of precision

        // TODO exercise 1: The train should loop through all the positions from the given route
        // TODO exercise 1: And once the last position was reached, restart with the first one

        // TODO exercise 1: You can have a look at the LightsTask for an example on how to set reported properties
    }
}
