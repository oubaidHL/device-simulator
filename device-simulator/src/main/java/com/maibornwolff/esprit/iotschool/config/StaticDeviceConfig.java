package com.maibornwolff.esprit.iotschool.config;

import com.microsoft.azure.sdk.iot.provisioning.device.ProvisioningDeviceClientTransportProtocol;

public class StaticDeviceConfig {

    // =============================================== static config ========================================

    /**
     * The public endpoint of the azure device provisioning service
     * This is always the same, regardless of what DPS you provisioned
     */
    public static final String GLOBAL_ENDPOINT = "global.azure-devices-provisioning.net";

    /**
     * ID of the DPS used for this project
     * Since we have one DPS for all groups, this does not need to be changed
     */
    public static final String ID_SCOPE = "0ne009F4983"; // iot-school-dps

    /**
     * Use this instead of 0ne009F4983, if you are in one of the groups
     * ain-sbanioria
     * jouajem
     * makrouth
     * mlabbes
     * mlawi
     * refissa
     * rouz-jerbi
     * tastira
     */
    // public static final String ID_SCOPE = "0ne00A11793"; // iot-school-dps-more

    /**
     * Protocol used for Device-Hub communication
     * In this school, we use MQTT
     */
    public static final ProvisioningDeviceClientTransportProtocol TRANSPORT_PROTOCOL = ProvisioningDeviceClientTransportProtocol.MQTT;

    /**
     * Time to wait for the device before trying to re-register with the hub
     */
    public static final int MAX_TIME_TO_WAIT_FOR_REGISTRATION_MS = 10_000;
}
