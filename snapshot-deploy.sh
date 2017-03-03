#!/bin/bash

if [ ! -z "$CODICE_PASSWORD" ]; then
  echo "The codice password was available in the environment, deploying snapshots to nexus."
  mvn clean deploy --settings ./travis_settings.xml
else
  echo "The codice password was not available in the environment, skipping deploy to nexus."
fi
