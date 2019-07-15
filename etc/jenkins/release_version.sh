#!/bin/bash -ex
#
# Arguments:
#  $1 - SPEC_VERSION
#  $2 - NEXT_SPEC_VERSION
#  $3 - API_VERSION
#  $4 - NEXT_API_VERSION

SPEC_VERSION="${1}"
NEXT_SPEC_VERSION="${2}"
API_VERSION="${3}"
NEXT_API_VERSION="${4}"

env | sort

pwd
ls -l etc
. etc/scripts/mvn.sh
read_version 'SPEC' "${SPEC_DIR}"
read_version 'API' "${API_DIR}/jaxb-api"
