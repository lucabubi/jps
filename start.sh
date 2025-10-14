#!/bin/bash

# Navigate to gatewayAPI directory
# shellcheck disable=SC2164
cd "./gatewayAPI"

# Build the gatewayAPI image
sudo ./gradlew bootBuildImage --imageName=g19/gateway_api
# Navigate to CRM directory
cd "../CRM"

# Build the CRM image
sudo ./gradlew bootBuildImage --imageName=g19/crm

cd "../document_store"
# Build the document_store image
sudo ./gradlew bootBuildImage --imageName=g19/document_store

# Navigate to communication_manager directory
cd "../communication_manager"

# Build the communication_manager image
sudo ./gradlew bootBuildImage --imageName=g19/communication_manager

cd "../analytics_crm"
# Build the analytics_crm image
sudo ./gradlew bootBuildImage --imageName=g19/analytics_crm

# Navigate to user-interface directory
cd "../user-interface/JobPlacementServices"

# Build the user-interface image
sudo docker build -t g19/user-interface .
# shellcheck disable=SC2103
cd ".."
cd ".."

docker network create jps-net
docker network ls | grep jps-net
# Run docker-compose
docker-compose -f gatewayAPI/compose_mac.yaml -f CRM/compose.yaml -f analytics_crm/compose.yaml -f communication_manager/compose.yaml -f document_store/compose.yaml -f user-interface/JobPlacementServices/compose.yaml up