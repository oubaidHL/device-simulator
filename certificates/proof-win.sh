#!/bin/bash
cd "$(dirname "${0}")" # go to the directory of this script

function usage () {
  cat << EOF >&2
  Usage:
    $(basename $0) subsidiary-name azure_token
  Parameters:
    subsidiary-name:  Name of the subsidiary to create this cert for
    azure_token:      Token requested by azure DPS
EOF
}

if [[ $# -lt 2 ]] ; then
  usage
  exit 1
fi

sub_id=${1}
azure_token=${2}

if [[ ! -f "subca/${sub_id}.cnf" ]]; then
  echo >&2 "Subsidiary ${sub_id} does not exist. Can not create device certificate."
  exit 1
fi

mkdir proof

# create a csr with that key and enter the token from azure
cat <<-EOF > "proof/${azure_token}.cnf"
[req]
default_bits = 2048
default_md = sha256
req_extensions = req_ext
distinguished_name = dn
[ dn ]
[ req_ext ]
subjectAltName = @alt_names
[alt_names]
DNS.1 = ${azure_token}
EOF

# subj path needs to be escaped on windows with MinGW
# https://stackoverflow.com/questions/31506158/running-openssl-from-a-bash-script-on-windows-subject-does-not-start-with
openssl req -new -newkey rsa:2048 -nodes -sha256 -keyout proof/pop.key -out proof/pop.csr -subj "//C=DE\ST=Bavaria\L=Munich\O=IoT School\OU=PKI Services\CN=${azure_token}/emailAddress=iotschool@maibornwolff.de" -config "proof/${azure_token}.cnf"

# sign the csr with the root ca to proof ownership
openssl ca -config "subca/${sub_id}.cnf" -in proof/pop.csr -out proof/pop.pem -extensions client_ext