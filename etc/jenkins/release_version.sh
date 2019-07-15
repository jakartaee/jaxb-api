#!/bin/bash

pwd
ls -l etc
. etc/scripts/mvn.sh
read_version 'SPEC' "${SPEC_DIR}"
read_version 'API' "${API_DIR}/jaxb-api"
