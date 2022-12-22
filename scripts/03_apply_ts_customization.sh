#!/bin/sh

cd /home/otadmin/helm_charts/teamsite/bundle/customization/

docker build ./build --rm -t tmcontainerimages.azurecr.io/ts-customization:22.2.0 --build-arg BASE_IMAGE=tmcontainerimages.azurecr.io/ts-alpine-base:22.2.0

docker create --name customizations tmcontainerimages.azurecr.io/ts-customization:22.2.0

docker cp $PWD/build/files/iwcustom.orig customizations:/iwcustom.orig

docker commit customizations tmcontainerimages.azurecr.io/ts-customization:22.2.0

docker rm customizations

cd /home/otadmin/Desktop/customization

# <vmname> (do not add .eimdemo.com)
./customizehostname.sh vm-demootts 


# cd /home/otadmin/helm_charts/teamsite/bundle/
# ./action.sh delete authoring runtime
# ./action.sh apply authoring runtime 

