# Sample
aws sns create-topic --name code-commit-topic
aws sns subscribe \
    --topic-arn arn:aws:sns:us-west-2:893703045818:code-commit-topic \
    --protocol email \
    --notification-endpoint harrajag@amazon.com