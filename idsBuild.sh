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
	 
echo Downloading Java 8...
wget --user=admin --password=$ADMIN_PASSWORD https://$BUILD_DOCKER_HOST:8443/jdk-8u65-x64.tar.gz -q
echo Extracting Java 8...
tar xzf jdk-8u65-x64.tar.gz
echo And removing the tarball...
rm jdk-8u65-x64.tar.gz
export JAVA_HOME=$(pwd)/jdk1.8.0_65
# echo $JAVA_HOME
echo Building projects using gradle...
./gradlew build
echo Building and Starting Concierge Docker Image...
cd concierge-wlpcfg
sed -i s/PLACEHOLDER_ADMIN_PASSWORD/$ADMIN_PASSWORD/g ./Dockerfile
../gradlew buildDockerImage
../gradlew removeCurrentContainer
../gradlew startNewContainer
echo Building and Starting Room Docker Image...
cd ../room-wlpcfg
sed -i s/PLACEHOLDER_ADMIN_PASSWORD/$ADMIN_PASSWORD/g ./Dockerfile
../gradlew buildDockerImage
../gradlew removeCurrentContainer
../gradlew startNewContainer
echo Removing JDK, cause there's no reason that's an artifact...
cd ..
rm -rf jdk1.8.0_65