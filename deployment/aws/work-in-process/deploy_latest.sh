#!/bin/bash

CLUSTER_NAME="your-cluster"
SERVICE_NAME="your-service"
CONTAINER_NAME="your-container"
IMAGE_URI="your-account-id.dkr.ecr.region.amazonaws.com/your-repo:latest"
TASK_DEF_FAMILY="your-task-def-family"

# Fetch existing task definition
TASK_DEF=$(aws ecs describe-task-definition --task-definition $TASK_DEF_FAMILY)
NEW_DEF=$(echo $TASK_DEF | jq '.taskDefinition')

# Register new revision with updated image
NEW_IMAGE_DEF=$(echo $NEW_DEF | jq --arg IMAGE "$IMAGE_URI" '.containerDefinitions[0].image = $IMAGE')
NEW_DEF_JSON=$(echo $NEW_IMAGE_DEF | jq '{family, networkMode, containerDefinitions, requiresCompatibilities, cpu, memory, executionRoleArn, taskRoleArn}')

aws ecs register-task-definition --cli-input-json "$(echo $NEW_DEF_JSON)"

# Force service update to deploy latest task
aws ecs update-service --cluster $CLUSTER_NAME --service $SERVICE_NAME --force-new-deployment
