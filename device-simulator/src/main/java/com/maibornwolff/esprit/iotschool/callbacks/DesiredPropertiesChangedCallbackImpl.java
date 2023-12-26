package com.maibornwolff.esprit.iotschool.callbacks;

import com.microsoft.azure.sdk.iot.device.twin.DesiredPropertiesCallback;
import com.microsoft.azure.sdk.iot.device.twin.Twin;
import com.microsoft.azure.sdk.iot.device.twin.TwinCollection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Supplier;

/**
 * Handler for changes to the desired properties of the device twin
 * Changes to the desired properties are made by external clients (e.g. backend api)
 * Then the device will react to it
 */
public class DesiredPropertiesChangedCallbackImpl implements DesiredPropertiesCallback {

    private final Supplier<Twin> getTwin;

    public DesiredPropertiesChangedCallbackImpl(Supplier<Twin> getTwin) {
        this.getTwin = getTwin;
    }

    @Override
    public void onDesiredPropertiesUpdated(Twin twin, Object context) {
        TwinCollection desiredProperties = twin.getDesiredProperties();
        ArrayList<String> properties = new ArrayList<>(desiredProperties.keySet());
        properties.sort(Comparator.naturalOrder());

        for (final String p : properties) {
            System.out.println("Desired '" + p + "': " + desiredProperties.get(p));
        }

        getTwin.get().getDesiredProperties().putAll(desiredProperties);
        getTwin.get().getDesiredProperties().setVersion(desiredProperties.getVersion());
    }
}