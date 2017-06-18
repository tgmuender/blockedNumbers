#!/usr/bin/env bash

set -euxo pipefail

SCRIPT_LOC="$(dirname $0)";

./gradlew shadowJar
java \
    -DblockedNumbersFileLocation="$SCRIPT_LOC/src/main/resources/blockedNumbers.json" \
    -jar \
    build/libs/blocked-numbers-server-1.0.0-SNAPSHOT-all.jar \
