###Step 1
#### Push image
``` bash
cd 1-ecr-image-push
./run.sh <<ecr-repository>>
```

#### Create AWS CodeCommit repository
```bash
aws codecommit create-repository --repository-name test-repo --repository-description "My Test repository"
```

###Step 2
#### Create VPC
```bash
cd 2-create-vpc
./run.sh
```

**Output**
```bash
aws-batch-cdk-vpc-efs-launch-template.privatesubnet = subnet-<<id>>
aws-batch-cdk-vpc-efs-launch-template.publicsubnet = subnet-<<id>>
aws-batch-cdk-vpc-efs-launch-template.vpcid = vpc-<<id>>
```

###Step 3
#### ECS Cluster & Task
```bash
export CDK_DEFAULT_ACCOUNT = <<aws_account_id>>
export CDK_DEFAULT_REGION = <<aws_region>>
cd 3-create-ecs-task
./run.sh <<vpc-id>> <<ecr-repo-uri>>
```

**Output**
```bash
aws-cdk-fargate-ecs.CLUSTERNAME = Fargate-Job-Cluster
aws-cdk-fargate-ecs.ClusterARN = <<cluster_arn>>
aws-cdk-fargate-ecs.ContainerARN = Fargate-Container
aws-cdk-fargate-ecs.TaskARN = <<task_arn>>
aws-cdk-fargate-ecs.TaskExecutionRole = <<execution_role_arn>>
aws-cdk-fargate-ecs.TaskRole = <<task_role_arn>>
```

###Step 4
####Create SNS Topic
```bash
aws sns create-topic --name code-commit-topic
```

####Create SNS Subscriber
```bash
aws sns subscribe \
    --topic-arn <<topic_arn>> \
    --protocol email \
    --notification-endpoint <<email_address>>
```

###Step 5
####Lambda & CodeCommit Trigger
```bash
export CDK_DEFAULT_ACCOUNT = <<aws_account_id>>
export CDK_DEFAULT_REGION = <<aws_region>>
cd 5-Lambda-CodeCommit-Trigger
./run.sh <<taskarn>> <<snstopicarn>> subnet-<<id>> <<codecommitarn>>
```
# event-driven-eks-fargate-scheduler
