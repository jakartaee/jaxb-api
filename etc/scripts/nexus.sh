# Drop old artifacts from staging repository
# Arguments:
#  $1 - Staging key value with grep REGEX prefixes
#  $2 - Build directory
drop_artifacts() {
  echo '-[ Drop old staging repository deployments ]------------------------------------'
  for staging_key in `(cd ${2} && mvn -B nexus-staging:rc-list | egrep "^\[INFO\] [A-Z,a-z,-]+-[0-9]+\s+[A-Z]+\s+${1}" | awk '{print $2}')`; do
    echo "Repository ID: ${staging_key}"
    (cd ${2} && \
      mvn -U -C \
          -DstagingRepositoryId="${staging_key}" \
          nexus-staging:rc-drop)
  done
}
