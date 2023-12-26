package com.maibornwolff.esprit.iotschool.callbacks;

import com.maibornwolff.esprit.iotschool.status.DeviceProvisioningStatus;
import com.microsoft.azure.sdk.iot.provisioning.device.ProvisioningDeviceClientRegistrationCallback;
import com.microsoft.azure.sdk.iot.provisioning.device.ProvisioningDeviceClientRegistrationResult;

/**
 * Handler for device provisioning updates
 * The DPS is called before connecting to the IOT hub
 * <p>
 * The handler will update the {@link DeviceProvisioningStatus} passed as context with the update from the DPS
 */
public class DeviceProvisioningDeviceClientRegistrationCallbackImpl implements ProvisioningDeviceClientRegistrationCallback {

    /**
     * The dps connection can be successful with a list of status, or fail with an exception
     *
     * @param provisioningDeviceClientRegistrationResult result in case of successful connection
     * @param exception                                  exception in case of a failure
     * @param context                                    external context, must be {@link DeviceProvisioningStatus}, will then contain the result
     */
    @Override
    public void run(ProvisioningDeviceClientRegistrationResult provisioningDeviceClientRegistrationResult, Exception exception, Object context) {
        if (context instanceof DeviceProvisioningStatus status) {
            status.provisioningDeviceClientRegistrationInfoClient = provisioningDeviceClientRegistrationResult;
            status.exception = exception;
        } else {
            System.err.println("> Received unknown context");
        }
    }
}