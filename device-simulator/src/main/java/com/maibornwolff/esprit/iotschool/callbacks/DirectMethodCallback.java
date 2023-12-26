package com.maibornwolff.esprit.iotschool.callbacks;

import com.maibornwolff.esprit.iotschool.methods.Method;
import com.microsoft.azure.sdk.iot.device.twin.DirectMethodPayload;
import com.microsoft.azure.sdk.iot.device.twin.DirectMethodResponse;
import com.microsoft.azure.sdk.iot.device.twin.MethodCallback;
import com.microsoft.azure.sdk.iot.device.twin.Twin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class DirectMethodCallback implements MethodCallback {

    private final Map<String, Method> registeredMethods;
    private final Supplier<Twin> deviceTwin;

    public DirectMethodCallback(List<Method> methods, Supplier<Twin> deviceTwin) {
        this.deviceTwin = deviceTwin;
        this.registeredMethods = new HashMap<>();
        for (final Method m : methods) {
            registeredMethods.put(m.getName(), m);
        }
    }

    @Override
    public DirectMethodResponse onMethodInvoked(String methodName, DirectMethodPayload methodPayload, Object context) {
        for (Map.Entry<String, Method> m : registeredMethods.entrySet()) {
            if (m.getKey().equals(methodName)) {

                return m.getValue().run(deviceTwin.get(), methodPayload);
            }
        }

        return new DirectMethodResponse(404, "Method '" + methodName + "' is not available for this device");
    }
}