#!/bin/bash

#export VERSION=`curl -s https://repo1.maven.org/maven2/org/keycloak/keycloak-server-dist/ | sed -e 's/<[^>]*>//g' | grep -i final | cut -d '/' -f1 | tail -n1`
export VERSION="5.0.0"
export KEYCLOAK="keycloak-${VERSION}"
