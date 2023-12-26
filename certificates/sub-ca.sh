#!/bin/bash
cd "$(dirname "${0}")" # go to the directory of this script

function usage () {
  cat << EOF >&2
  Usage:
    $(basename $0) subsidiary-name
  Parameters:
    subsidiary-name: name of the subsidiary to create. Use a human readable name without spaces, e.g. "firstclass"
EOF
}

if [[ $# -lt 1 ]] ; then
  usage
  exit 1
fi

sub_id=${1}

# init
mkdir subca/certs subca/db
touch subca/db/index
openssl rand -hex 16 > subca/db/serial
echo 1001 > subca/db/crlnumber

# init cert number in root
openssl rand -hex 16 > rootca/db/serial

# copy config and add current id
cat "subca/sub-ca.cnf" | sed "s/sub-ca/${sub_id}/g" > "subca/${sub_id}.cnf"

# new certificate signing request 
openssl req -new -config "subca/${sub_id}.cnf" -out "subca/${sub_id}.csr" -keyout "subca/${sub_id}.key"

# sign the request with its own key
openssl ca -config rootca/root-ca.cnf -in "subca/${sub_id}.csr" -out "subca/${sub_id}.pem" -extensions sub_ca_ext