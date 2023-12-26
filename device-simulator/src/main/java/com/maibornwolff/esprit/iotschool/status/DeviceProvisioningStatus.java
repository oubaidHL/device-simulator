package com.maibornwolff.esprit.iotschool.status;

import com.microsoft.azure.sdk.iot.provisioning.device.ProvisioningDeviceClientRegistrationResult;

/**
 * Track the status of a connection attempt to the DPS
 */
public class DeviceProvisioningStatus {
    public ProvisioningDeviceClientRegistrationResult provisioningDeviceClientRegistrationInfoClient = new ProvisioningDeviceClientRegistrationResult();
    public Exception exception;
}