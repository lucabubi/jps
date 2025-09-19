[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-24ddc0f5d75046c5622901739e7c5dd533143b0c8e959d652212380cedb1ea36.svg)](https://classroom.github.com/a/58TiBSsz)

We added CRM and communication_manager modules to the project. communication_manager has a "depends on" clausole in the docker-compose file, so it will be started after CRM2. CRM2 has been imported from the lab3 and modified to include APIs, dtos, entites and services developed for lab2. In order to run communication_manager, you firstly need to build the images and then run the communication_manager module:

Into CRM directory
[sudo] ./gradlew bootBuildImage --imageName=g19/crm

Into communication_manager directory
[sudo] ./gradlew bootBuildImage --imageName=g19/communication_manager

Into external lab5-g19 directory
docker-compose -f CRM2/compose.yaml -f communication_manager/compose.yaml up

or, alternatively, if you're using windows just run the following command in the external directory: ./start.ps1

If you are currently running an Unix-like system (macOS, GNU-Linux distros), you may have to build the images as root (sudo) in order to avoid permission issues, then start the container as standard user.
