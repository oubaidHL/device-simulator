package com.maibornwolff.esprit.iotschool.tasks;

import com.microsoft.azure.sdk.iot.device.twin.Twin;

public interface Task {

    public void run(final Twin deviceTwin);
}
