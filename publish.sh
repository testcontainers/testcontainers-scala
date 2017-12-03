#!/bin/bash
set -e

scalaVersions="2.11.8 2.12.3"

for version in $scalaVersions
do
    printf "\n========Run tests for scala $version==========\n"
    ./gradlew clean -Pscala=$version test integrationTest
done

for version in $scalaVersions
do
    printf "\n========Upload archives for scala $version==========\n"
    ./gradlew clean -Pscala=$version uploadArchives
done

./gradlew release