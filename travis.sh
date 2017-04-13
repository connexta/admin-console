#!/bin/bash

if [ "$TRAVIS_PULL_REQUEST" == "false" ]; then
    mvn clean install sonar:sonar
else
    mvn clean install sonar:sonar -Dsonar.analysis.mode=preview \
              -Dsonar.github.pullRequest=$TRAVIS_PULL_REQUEST \
              -Dsonar.github.repository=$TRAVIS_REPO_SLUG \
              -Dsonar.github.oauth=$GITHUB_TOKEN \
              -Dsonar.host.url=https://sonarqube.com \
              -Dsonar.login=$SONARQ_TOKEN
fi
