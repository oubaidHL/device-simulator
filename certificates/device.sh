#!/bin/bash
cd "$(dirname "${0}")" # go to the directory of this script

function usage () {
  cat << EOF >&2
  Usage:
    $(basename $0) subsidiary-name device-id
  Parameters:
    subsidiary-name:  Name of the subsidiary to create this cert for
    device-id:        Identifier of the device to create
EOF
}

if [[ $# -lt 2 ]] ; then
  usage
  exit 1
fi

sub_id=${1}
device_id=${2}

if [[ ! -f "subca/${sub_id}.cnf" ]]; then
  echo >&2 "Subsidiary ${sub_id} does not exist. Can not create device certificate."
  exit 1
fi

mkdir device

# create a csr with that key and enter the token from azure
openssl req -new -newkey rsa:2048 -nodes -sha256 -keyout "device/${sub_id}-${device_id}.key" -out "device/${sub_id}-${device_id}.csr" -subj "/C=DE/ST=Bavaria/L=Munich/O=IoT School/OU=PKI Services/CN=${device_id}/emailAddress=iotschool@maibornwolff.de" -config <(
cat <<-EOF
[req]
default_bits = 2048
default_md = sha256
req_extensions = req_ext
distinguished_name = dn
[ dn ]
[ req_ext ]
subjectAltName = @alt_names
[alt_names]
DNS.1 = ${device_id}
EOF
)

# sign the csr with the sub ca
openssl ca -config "subca/${sub_id}.cnf" -in "device/${sub_id}-${device_id}.csr" -out "device/${sub_id}-${device_id}.pem" -extensions client_ext

# add sub ca to chain
cat subca/${sub_id}.pem >> "device/${sub_id}-${device_id}.pem"

openssl pkcs12 -export -out "device/${sub_id}-${device_id}.pfx" -inkey "device/${sub_id}-${device_id}.key" -in "device/${sub_id}-${device_id}.pem"