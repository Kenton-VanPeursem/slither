#!/bin/bash
exitcode=$(./gradlew shadowJar)

failed=$(echo $exitcode | grep -in "fail")
if [[ -z $failed ]]; then
    java -jar slither/build/libs/slither-all.jar
else
    echo "Failed to build slither jar..."
fi
