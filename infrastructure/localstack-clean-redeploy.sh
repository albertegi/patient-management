#!/bin/bash
# LOCALSTACK ONLY - All AWS commands use localhost:4566. No real AWS is used.
#
# LocalStack cannot update ECS services in place. To apply config changes (e.g. JWT_SECRET fix),
# you must delete the stack and redeploy from scratch.
#
# IMPORTANT: Stop Docker Compose first (docker compose down) to free ports 8085-8088, 8333, 9096.
# Port conflicts cause LocalStack to assign random ports, breaking service communication.

set -e

# LocalStack requires these dummy credentials (real AWS keys cause InvalidAccessKeyId)
export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_DEFAULT_REGION=us-east-1

ENDPOINT="http://localhost:4566"
STACK_NAME="patient-management"
S3_BUCKET="patient-management-cf-templates"

echo "Deleting stack '$STACK_NAME'..."
aws --endpoint-url=$ENDPOINT cloudformation delete-stack --stack-name $STACK_NAME

echo "Waiting for stack deletion (this may take 1-2 minutes)..."
aws --endpoint-url=$ENDPOINT cloudformation wait stack-delete-complete --stack-name $STACK_NAME

echo "Creating S3 bucket in LocalStack (required when template > 51,200 bytes)..."
aws --endpoint-url=$ENDPOINT s3 mb s3://$S3_BUCKET 2>/dev/null || true

echo "Uploading template to LocalStack S3..."
TEMPLATE_KEY="patient-management-$(date +%s).template.json"
aws --endpoint-url=$ENDPOINT s3 cp "./cdk.out/localstack.template.json" "s3://$S3_BUCKET/$TEMPLATE_KEY"

echo "Deploying fresh stack..."
TEMPLATE_URL="http://s3.localhost.localstack.cloud:4566/$S3_BUCKET/$TEMPLATE_KEY"
aws --endpoint-url=$ENDPOINT cloudformation create-stack \
    --stack-name $STACK_NAME \
    --template-url "$TEMPLATE_URL" \
    --capabilities CAPABILITY_NAMED_IAM CAPABILITY_IAM

echo "Waiting for stack creation..."
aws --endpoint-url=$ENDPOINT cloudformation wait stack-create-complete --stack-name $STACK_NAME

echo ""
echo "Deployment complete. Load balancer:"
LB_DNS=$(aws --endpoint-url=$ENDPOINT elbv2 describe-load-balancers --query "LoadBalancers[0].DNSName" --output text)
echo "  http://$LB_DNS:8333/auth/login"
