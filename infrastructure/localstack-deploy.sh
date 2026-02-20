#!/bin/bash
# LOCALSTACK ONLY - All AWS commands use localhost:4566. No real AWS is used.
#
# Before running:
#   1. docker compose down   (free ports 8085-8088, 8333, 9096)
#   2. Rebuild images: mvn -f .. clean package -DskipTests
#   3. LocalStack must be running (e.g. docker compose up localstack)

set -e # Stops the script if any command fails

# LocalStack requires these dummy credentials (real AWS keys cause InvalidAccessKeyId)
export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_DEFAULT_REGION=us-east-1

ENDPOINT="http://localhost:4566"
S3_BUCKET="patient-management-cf-templates"

# Create S3 bucket in LocalStack (required when template > 51,200 bytes)
aws --endpoint-url=$ENDPOINT s3 mb s3://$S3_BUCKET 2>/dev/null || true

# Upload template manually (cloudformation deploy's internal S3 upload ignores --endpoint-url)
TEMPLATE_KEY="patient-management-$(date +%s).template.json"
aws --endpoint-url=$ENDPOINT s3 cp "./cdk.out/localstack.template.json" "s3://$S3_BUCKET/$TEMPLATE_KEY"

# Deploy using the pre-uploaded template URL (deploy command doesn't support --template-url)
TEMPLATE_URL="http://s3.localhost.localstack.cloud:4566/$S3_BUCKET/$TEMPLATE_KEY"
if aws --endpoint-url=$ENDPOINT cloudformation describe-stacks --stack-name patient-management &>/dev/null; then
  aws --endpoint-url=$ENDPOINT cloudformation update-stack \
    --stack-name patient-management \
    --template-url "$TEMPLATE_URL" \
    --capabilities CAPABILITY_NAMED_IAM CAPABILITY_IAM 2>/dev/null || echo "(No updates to apply)"
else
  aws --endpoint-url=$ENDPOINT cloudformation create-stack \
    --stack-name patient-management \
    --template-url "$TEMPLATE_URL" \
    --capabilities CAPABILITY_NAMED_IAM CAPABILITY_IAM
fi
# Wait for stack to be ready
aws --endpoint-url=$ENDPOINT cloudformation wait stack-create-complete --stack-name patient-management 2>/dev/null || \
aws --endpoint-url=$ENDPOINT cloudformation wait stack-update-complete --stack-name patient-management 2>/dev/null || true

LB_DNS=$(aws --endpoint-url=$ENDPOINT elbv2 describe-load-balancers \
    --query "LoadBalancers[0].DNSName" --output text)
echo ""
echo "Load balancer: http://$LB_DNS:8333"
echo "Auth login:    curl -X POST http://$LB_DNS:8333/auth/login -H 'Content-Type: application/json' -d '{\"username\":\"admin\",\"password\":\"admin123\"}'"