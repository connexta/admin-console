#!/bin/bash

DEPLOY_BRANCHES="master"

is-password-available() {
  if [ ! -z "$CODICE_PASSWORD" ]; then
    echo "The codice password was available in the environment."
    return 0
  else
    echo "The codice password was not available in the environment."
    return 1
  fi
}

is-deploy-branch() {
  local branch=$(git rev-parse --abbrev-ref HEAD)

  if echo $DEPLOY_BRANCHES | grep -w $branch > /dev/null; then
    echo "The branch $branch is a deploy branch."
    return 0
  else
    echo "The branch $branch is NOT a deploy branch."
    return 1
  fi
}

filter-mvn-output() {
  grep -v 'Generating\|Downloading:\|Downloaded:'
}

if is-password-available && is-deploy-branch; then
  echo "Deploying snapshots to nexus."
  mvn clean deploy sonar:sonar --settings ./travis_settings.xml
else
  echo "Skipping deploy to nexus."
  mvn clean install sonar:sonar | filter-mvn-output
fi
