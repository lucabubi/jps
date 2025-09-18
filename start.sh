#!/bin/bash

# Navigate to CRM2 directory
cd "./CRM2"

# Build the CRM2 image
sudo ./gradlew bootBuildImage --imageName=g19/crm2

# Navigate to communication_manager directory
cd "../communication_manager"

# Build the communication_manager image
sudo ./gradlew bootBuildImage --imageName=g19/communication_manager

# Navigate back to the lab5 directory
cd ".."

# Run docker-compose
docker-compose -f CRM2/compose.yaml -f communication_manager/compose.yaml up