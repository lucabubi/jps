# Navigate to gatewayAPI directory
Set-Location -Path "./gatewayAPI"

# Build the gatewayAPI image
./gradlew bootBuildImage --imageName=g19/gateway_api

# Navigate to CRM directory
Set-Location -Path "../CRM"

# Build the CRM image
./gradlew bootBuildImage --imageName=g19/crm

# Navigate to analytics_crm directory
Set-Location -Path "../analytics_crm"

# Build the analytics_crm image
./gradlew bootBuildImage --imageName=g19/analytics_crm

# Navigate to communication_manager directory
Set-Location -Path "../communication_manager"

# Build the communication_manager image
./gradlew bootBuildImage --imageName=g19/communication_manager

Set-Location -Path "../user-interface/JobPlacementServices"

docker build -t g19/user-interface .

# Navigate back to the lab5 directory
Set-Location -Path "../../"

# Create a Docker network
docker network create jps-net
# Run docker-compose
docker-compose -f gatewayAPI/compose.yaml -f CRM/compose.yaml -f analytics_crm/compose.yaml -f communication_manager/compose.yaml -f user-interface/JobPlacementServices/compose.yaml up