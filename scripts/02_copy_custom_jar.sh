#!/bin/sh

clear

#JAR="OTTeamSiteDCRRetriever20.4.7.jar"
JAR="OTTeamSiteLSDSRetriever22.2.jar"
echo $JAR

SOURCE_PATH="/home/otadmin/Downloads/"
echo $SOURCE_PATH

CUSTOM_JAR="${SOURCE_PATH}${JAR}"
echo $CUSTOM_JAR

cd /home/otadmin/helm_charts/teamsite/bundle/customization/build/files/iwcustom.orig/app-server/Interwoven/ApplicationContainer/standalone/deployments

echo "Change dir to: ${PWD}"


cp $CUSTOM_JAR "./iw-cc.war/WEB-INF/lib/${JAR}" 
cp $CUSTOM_JAR "./iw-preview.war/WEB-INF/lib/${JAR}" 
cp $CUSTOM_JAR "./iw-wcmweb.ear/iw-teamsite.war/WEB-INF/lib/${JAR}" 
cp $CUSTOM_JAR "./wcm-service.war/WEB-INF/lib/${JAR}"


mkdir -p /home/otadmin/helm_charts/teamsite/bundle/customization/build/files/iwcustom.orig/lsds/Interwoven/LiveSiteDisplayServices/runtime/web/WEB-INF/lib/

cp $CUSTOM_JAR /home/otadmin/helm_charts/teamsite/
cp $CUSTOM_JAR /home/otadmin/helm_charts/teamsite/bundle/customization/build/files/iwcustom.orig/lsds/Interwoven/LiveSiteDisplayServices/runtime/web/WEB-INF/lib/
