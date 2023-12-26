#!/bin/bash
cd "$(dirname "${0}")" # go to the directory of this script

# init
mkdir rootca/certs rootca/db
touch rootca/db/index
openssl rand -hex 16 > rootca/db/serial
echo 1001 > rootca/db/crlnumber


# new certificate signing request 
openssl req -new -config rootca/root-ca.cnf -out rootca/root-ca.csr -keyout rootca/root-ca.key

# sign the request with its own key
openssl ca -selfsign -config rootca/root-ca.cnf -in rootca/root-ca.csr -out rootca/root-ca.pem -extensions ca_ext