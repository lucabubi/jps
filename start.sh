#!/bin/bash

# Navigate to gatewayAPI directory
cd "./gatewayAPI"

# Build the gatewayAPI image
sudo ./gradlew bootBuildImage --imageName=g19/gateway_api --platform linux/amd64

# Navigate to CRM directory
cd "../CRM"

# Build the CRM image
sudo ./gradlew bootBuildImage --imageName=g19/crm --platform linux/amd64

# Navigate to communication_manager directory
cd "../communication_manager"

# Build the communication_manager image
sudo ./gradlew bootBuildImage --imageName=g19/communication_manager --platform linux/amd64

# Navigate to user-interface directory
cd "../user-interface/JobPlacementServices"

# Build the user-interface image
sudo docker build -t g19/user-interface . --platform linux/amd64
cd ".."
cd ".."

# Run docker-compose
docker-compose -f gatewayAPI/compose.yaml -f CRM/compose.yaml -f communication_manager/compose.yaml -f user-interface/JobPlacementServices/compose.yaml up