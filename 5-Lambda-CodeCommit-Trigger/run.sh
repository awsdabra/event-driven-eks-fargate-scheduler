mvn compile
cdk deploy --require-approval=never -c taskarn=$1 \
-c snstopic=$2 \
-c subnet=$3 \
-c coderepo=$4