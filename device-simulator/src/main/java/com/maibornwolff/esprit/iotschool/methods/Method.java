package com.maibornwolff.esprit.iotschool.methods;

import com.microsoft.azure.sdk.iot.device.twin.DirectMethodPayload;
import com.microsoft.azure.sdk.iot.device.twin.DirectMethodResponse;
import com.microsoft.azure.sdk.iot.device.twin.Twin;

/**
 * Interface defining a remote method call handler
 * This can be invoked by the backend app or via the azure ui
 * <p>
 * Every method handler reacts to methods with it's name
 * And sends the given DirectMethodResponse to the caller
 */
public interface Method {

    /**
     * Identifier of the method
     *
     * @return the name of this method. will only react to that name
     */
    public String getName();

    /**
     * Actual implementation
     * This can perform changes on the device twin
     *
     * @param deviceTwin current status of the device
     * @param payload    a "simple value" like string, or int, or a json object
     * @return response to send to the caller (includes status and message)
     */
    public DirectMethodResponse run(final Twin deviceTwin, DirectMethodPayload payload);
}
