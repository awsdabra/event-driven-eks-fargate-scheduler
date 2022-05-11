# Create ECR repo and push docker image
## Create ECR repository
Run the following command to create new ECR repository:
```bash 
aws ecr create-repository --repository-name scan-repo
``` 

**Output**
```bash
{
    "repository": {
        "repositoryArn": "arn:aws:ecr:us-west-2:893703045818:repository/scan-repo",
        "registryId": "893703045818",
        "repositoryName": "scan-repo",
        "repositoryUri": "893703045818.dkr.ecr.us-west-2.amazonaws.com/scan-repo",
        "createdAt": "2020-11-02T08:18:47-08:00",
        "imageTagMutability": "MUTABLE",
        "imageScanningConfiguration": {
            "scanOnPush": false
        },
        "encryptionConfiguration": {
            "encryptionType": "AES256"
        }
    }
}
```

Login to the new docker repository
- Get login command for the newly created docker repository and run the command to explicitly login. 
Make sure to update "aws_account_id" in the login command
```bash
aws ecr get-login-password --region region | docker login --username AWS \
--password-stdin aws_account_id.dkr.ecr.region.amazonaws.com
```


## Push docker image to ECR
Run the following command with the ECR ```repositoryUri``` as argument:
```bash
./run.sh 893703045818.dkr.ecr.us-west-2.amazonaws.com/scan-repo
```
It will take care of creating a docker image and pushing it to ECR repository
