package com.maibornwolff.esprit.iotschool.certs;

import com.microsoft.azure.sdk.iot.provisioning.security.SecurityProvider;
import com.microsoft.azure.sdk.iot.provisioning.security.hsm.SecurityProviderX509Cert;

import java.io.IOException;
import java.security.Key;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;

/**
 * Generate certificates using
 * ./device.sh tunis-lac2 station-airport-route
 * <p>
 * and then copy them to the resources directory, e.g.
 * cp device/tunis-lac2-station-airport-route.key ~/git/iotschool/tunis-iot-school/device-provisioning/src/main/resources/certs/device.key
 * cp device/tunis-lac2-station-airport-route.pem ~/git/iotschool/tunis-iot-school/device-provisioning/src/main/resources/certs/device.pub
 */
public class SecurityProviderBuilder {

    /**
     * Build the cert handler for the IOT connection
     *
     * @param certificateName the name (prefix) to use for connection.
     *                        Certificates need to be stored in `resources/certs` and be called
     *                        *certificateName*.key and *certificateName*.pub, e.g. device.key and device.pub by default
     */
    public SecurityProvider build(final String certificateName) {
        // read certificates
        try {
            X509Certificate leafPublicCert = CertUtils.parsePublicKeyCertificate(certificateName + ".pub");
            Key leafPrivateKey = CertUtils.parsePrivateKey(certificateName + ".key");
            return new SecurityProviderX509Cert(leafPublicCert, leafPrivateKey, Collections.emptyList());
        } catch (IOException e) {
            System.err.println("> Failed to read the certificates, check your resources/certs folder.");
            e.printStackTrace();
        } catch (CertificateException e) {
            System.err.println("> Failed to parse the certificates, check the content of your files in the resources/certs folder.");
            e.printStackTrace();
        }
        return null;
    }
}
