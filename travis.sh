#!/bin/bash
export MAVEN_OPTS=-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn
if [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ $TRAVIS_BRANCH == "master" ]; then
  echo "Release"
else 
  mvn clean verify jacoco:report coveralls:report sonar:sonar -Pitests -DrepoToken=$COVERALLS_TOKEN -Dsonar.projectKey=Metatavu_metamind-api --batch-mode -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn
fi