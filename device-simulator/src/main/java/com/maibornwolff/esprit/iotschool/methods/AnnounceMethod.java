package com.maibornwolff.esprit.iotschool.methods;

import com.microsoft.azure.sdk.iot.device.twin.DirectMethodPayload;
import com.microsoft.azure.sdk.iot.device.twin.DirectMethodResponse;
import com.microsoft.azure.sdk.iot.device.twin.Twin;

/**
 * This will react to the "announce" method call
 * And will log the payload (message) to the stdout
 */
public class AnnounceMethod implements Method {

    @Override
    public String getName() {
        return "announce";
    }

    @Override
    public DirectMethodResponse run(Twin deviceTwin, DirectMethodPayload payload) {
        // TODO exercise 3: Log the messaged included in the method call to stdout
        // TODO exercise 3: Then send a successful response instead of 404 and also include the string that was logged

        return new DirectMethodResponse(404, "TODO exercise 3: NOT YET IMPLEMENTED");
    }
}
