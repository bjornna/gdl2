#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
     #mvn release:clean release:prepare release:perform -B -e --settings settings.xml
     mvn deploy --settings settings.xml
fi