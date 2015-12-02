#!/bin/bash

#
# This script is only intended to run in the IBM DevOps Services Pipeline Environment.
#

#!/bin/bash
echo Informing slack...
curl -X 'POST' --silent --data-binary '{"text":"A new build for the room service has started."}' $WEBHOOK > /dev/null

mkdir dockercfg ; cd dockercfg
echo Downloading Docker requirements..
wget --user=admin --password=$ADMIN_PASSWORD https://$BUILD_DOCKER_HOST:8443/dockerneeds.tar -q
echo Setting up Docker...
tar xzf dockerneeds.tar
cd .. 
	 
echo Building projects using gradle...
./gradlew build
echo Building and Starting Concierge Docker Image...
cd concierge-wlpcfg
../gradlew buildDockerImage
../gradlew stopCurrentContainer
../gradlew removeCurrentContainer
../gradlew startNewContainer

echo Building and Starting Room Docker Image...
cd ../room-wlpcfg
../gradlew buildDockerImage
../gradlew stopCurrentContainer
../gradlew removeCurrentContainer
../gradlew startNewContainer

rm -rf dockercfg