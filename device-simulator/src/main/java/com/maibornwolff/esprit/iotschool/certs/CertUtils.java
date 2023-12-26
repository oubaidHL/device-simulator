package com.maibornwolff.esprit.iotschool.certs;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.Key;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Helpers to read pem files (key and/or certificate) from the resources directory
 */
public class CertUtils {

    /**
     * Read a .pem file containing a public certificate from a given file name in src/main/resources/certs
     *
     * @param certName name of the file
     * @return X509 Cert
     * @throws IOException          in case the file does not exist
     * @throws CertificateException in case the file is not a valid pem and/or public certificate
     */
    public static X509Certificate parsePublicKeyCertificate(String certName) throws IOException, CertificateException {
        Security.addProvider(new BouncyCastleProvider());
        PemReader publicKeyCertificateReader = new PemReader(new FileReader(ResourceUtils.getFile("classpath:certs/" + certName)));
        PemObject possiblePublicKeyCertificate = publicKeyCertificateReader.readPemObject();
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(possiblePublicKeyCertificate.getContent()));
    }


    /**
     * Read a .pem file containing a private key from a given file name in src/main/resources/certs
     *
     * @param certName name of the file
     * @return X509 Key
     * @throws IOException  in case the file does not exist or is not a valid private key
     * @throws PEMException in case the file is not a valid pem and/or private key
     */
    public static Key parsePrivateKey(final String certName) throws IOException {
        Security.addProvider(new BouncyCastleProvider());
        PEMParser privateKeyParser = new PEMParser(new FileReader(ResourceUtils.getFile("classpath:certs/" + certName)));
        Object possiblePrivateKey = privateKeyParser.readObject();
        return getPrivateKey(possiblePrivateKey);
    }

    private static Key getPrivateKey(Object possiblePrivateKey) throws PEMException {
        if (possiblePrivateKey instanceof PEMKeyPair) {
            return new JcaPEMKeyConverter()
                    .getKeyPair((PEMKeyPair) possiblePrivateKey)
                    .getPrivate();
        } else if (possiblePrivateKey instanceof PrivateKeyInfo) {
            return new JcaPEMKeyConverter().getPrivateKey((PrivateKeyInfo) possiblePrivateKey);
        } else {
            throw new PEMException("Unable to parse private key, type unknown");
        }
    }
}
