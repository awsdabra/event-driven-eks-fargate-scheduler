export CDK_DEFAULT_ACCOUNT=893703045818
export CDK_DEFAULT_REGION=us-west-2
mvn compile
cdk deploy --require-approval=never -c vpc-id=$1 -c image=$2