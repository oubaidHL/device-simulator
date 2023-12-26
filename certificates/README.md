# Certificates

# Create root and intermediate certificates
New devices are connected to their corresponding IoT Hub by the Device Provisioning Service (DPS). 
To ensure that only authorized devices are provisioned, symmetric or asymmetric authentication mechanisms can be implemented by the DPS to authorize devices.

## Symmetric vs Asymmetric

### Symmetric
For symmetric authorization methods, both the client (device) and the server (dps) know the same secret key/password.
When a device wants to connect to the dps, it sends its password (or a hash of it) to the dps, and the dps checks if it is the correct one.

While this works fine for a single device, we run into issues when we want to provision multiple devices.

Now we could give the same password to all devices. 
But then we have no way of distinguishing between them. 
E.g. we can not make sure that a device is what it claims to be. 
Also we can not easily block one of the devices, because if we invalidate the password, all devices will get blocked.

Another option would be to add a single password for each devices.
This way we can identify each device by its password and also block a single one.
But this means we need to add a lot of passwords to the dps and keep track of them.
And we need to constantly extend that list when new devices are built.

### Asymmetric
An asymmetric method solves the issues mentioned above, but is a bit more complicated.
Instead of one shared password, we now have layers of certificates/passwords.
Each layer can be used to validate all certificates on the layer below it.

So the dps will have a root or subsidiary (since the root is normally kept in the background) certificate.
This is then used to generate a list of device certificates.
Each device certificate will contain a unique identifier (e.g. device name).
The device certificates are then signed with the root/intermediate certificate, to make sure they are not tempered with.

This way, we can easily generate new certificates without adding a new key to the dps.
Each device will present its unique certificate to the dps and the dps checks if the signature is valid (i.e. nothing was changed in the certificate) and if that passes it will accept the device.

Invalidating a certificate is a bit more complicated, since the signature will stay valid, so usually a certificate revocation list is used to explicitely track invalid certificates.

## Generating certificates
For this demo we will use the same root certificate for all our teams. The root ca is only used to generate two intermediate certificates and you don't have too look at it.
We will also use the same 2 itermediate certs for all teams. Those are stored in the git repository in the `subca` directory.

Device certificates need to be generated for each simulated device in each team. For this we have created a small bash script `device.sh` in the git repository, that can be used like
```bash
./device.sh <subca> <deviceid>

# E.g.
./device.sh firstclass wagon14
./device.sh secondclass wagon8
```
for each usage it will generate 4 new files in the `device` directory.
* `firstclass-wagon14.csr`: A certificate signing request that will be sent to the intermediate ca (normally on a different server) for signing
* `firstclass-wagon14.key`: The private key that the device will use to decrypt messages sent to it
* `firstclass-wagon14.pem`: The public cert that the device will use to authenticate to the dps (it contains the device identifier and is signed by the intermediate ca) and that the dps will use to send messages to the device
* `firstclass-wagon14.pfx`: A combined key-cert file used in the C# docker container

To use this script, do
1. Clone the git repository
2. Go to the `pki` directory
3. Check that there is a script `device.sh`
4. Check that there is directory `subca`
5. Check that the `subca` contains a `firstclass.pem`, `firstclass.key`, `secondclass.pem` and `secondclass.key`
6. Go to the `pki` directory
7. Run the `device.sh` to generate a certificate for your device

## Add intermediate certificates to DPS and create provisioning rules
Once you have all necessary certificates, you can configure our dps to use the intermediate certificate to verify the device certs.
To do so, go to the DPS in azure, and add two new Provisioning rules.
1. Add first rule
2. Name it "First class"
3. Upload the `subca/firstclass.pem` file
4. Link the provisioning rule to the "firstclass" hub
5. Add another rule
6. Name it "Second class"
7. Upload the `subca/secondclass.pem` file
8. Link the provisioning rule to the "secondclass" hub

You will see that the rules are both marked as "Unverified"
This is because we only uploaded the `.pem` certificate. But not the actual private key for it. So azure can not be sure, that we have the private key.
To "proof" that we do, we need to sign a special device certificate that azure requests.

So click on the "Validate" button to get the unique identifier for the device azure wants us to verify.
Copy the id, and run the `proof.sh` script like this
`./proof.sh firstclass <azurechallengeid>`
(Change "firstclass" with "secondclass" for the other rule)
Then upload the resulting `proof/firstclass-proof.pem` to the azure portal and it should change to "Validated".

## Create device certificates from intermediate certificates
Run the `device.sh` again to generate more certificates for your device.
Devices generated with the `firstclass` ca will be assigned to the "firstclass" IoT Hub, those with `seondclass` to the "secondclass" hub based on their certificate without any further config necessary.