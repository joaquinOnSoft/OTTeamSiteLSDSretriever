#!/bin/sh

clear

cd /home/otadmin/helm_charts/teamsite/bundle/customization/build/files/iwcustom.orig/app-server/Interwoven/ApplicationContainer/standalone/deployments

echo "Change dir to: ${PWD}"

for JAR_FILE in $(find . -name OT*.jar)
do
    echo -e "\n${JAR_FILE}"
    rm $JAR_FILE
done

echo "Check deletion"
find . -name OT*.jar
