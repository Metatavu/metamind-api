#!/bin/bash

if [ ! -f sonar-scanner/bin/sonar-scanner ]; then
  rm -fR sonar-scanner
  wget "https://sonarsource.bintray.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-3.0.3.778-linux.zip"
  unzip sonar-scanner-cli-3.0.3.778-linux.zip
  mv sonar-scanner-3.0.3.778-linux sonar-scanner
fi;