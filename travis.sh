#!/bin/bash

if [ "$TRAVIS_PULL_REQUEST" != "false" ] && [ $TRAVIS_BRANCH != "master" ] && [ -n "${GITHUB_TOKEN}" ] && [ -n "${SONAR_TOKEN}" ]; then
  echo "Pull request"
  mvn clean test -DrepoToken=$COVERALLS_TOKEN -DskipCoverage=true
  TEST_STATUS=$?
  PROJECT_VERSION=`cat pom.xml|grep version -m 1|sed -e 's/.*<version>//'|sed -e 's/<.*//'`
    
  sh sonar-scanner/bin/sonar-scanner -Dsonar.host.url=$SONAR_HOST_URL \
    -Dsonar.analysis.mode=issues \
    -Dsonar.login=$SONAR_TOKEN \
    -Dsonar.projectKey=$SONAR_PROJECT_KEY \
    -Dsonar.projectName=Metamind\ API \
    -Dsonar.projectVersion=$PROJECT_VERSION \
    -Dsonar.sources=src \
    -Dsonar.java.binaries=target/classes \
    -Dsonar.java.source=1.8 \
    -Dsonar.github.oauth=$GITHUB_TOKEN \
    -Dsonar.github.repository=$TRAVIS_REPO_SLUG \
    -Dsonar.github.pullRequest=$TRAVIS_PULL_REQUEST
  
  exit $TEST_STATUS
  
elif [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ $TRAVIS_BRANCH == "develop" ]; then

  echo "Develop build"

  mvn clean install
  TEST_STATUS=$?
  
  if [ "$TEST_STATUS" != "0" ]; then
    echo "Build failed"
  else
    PROJECT_VERSION=`cat pom.xml|grep version -m 1|sed -e 's/.*<version>//'|sed -e 's/<.*//'`
    
    sh sonar-scanner/bin/sonar-scanner -Dsonar.host.url=$SONAR_HOST_URL \
      -Dsonar.analysis.mode=publish \
      -Dsonar.login=$SONAR_TOKEN \
      -Dsonar.projectKey=$SONAR_PROJECT_KEY \
      -Dsonar.projectName=Metamind\ API \
      -Dsonar.projectVersion=$PROJECT_VERSION \
      -Dsonar.sources=src \
      -Dsonar.java.binaries=target/classes \
      -Dsonar.java.source=1.8
  fi
  
  exit $TEST_STATUS
elif [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ $TRAVIS_BRANCH == "master" ]; then
  echo "Master build"
else
  echo "Push to branch" 	  
fi
