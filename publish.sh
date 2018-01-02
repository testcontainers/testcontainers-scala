#!/bin/bash
set -e

read -p "Is it $(head -n 1 gradle.properties) [y/[new version]]?" version
if [ version != "y" ]; then
   fileTail=$(tail -n +2 gradle.properties)
   echo "version = $version" > gradle.properties
   echo "${fileTail}" >> gradle.properties
fi

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