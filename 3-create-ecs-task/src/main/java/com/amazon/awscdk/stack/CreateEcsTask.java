package com.amazon.awscdk.stack;

import com.amazon.cdk.Constants;
import com.amazon.cdk.RoleUtils;
import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.core.CfnOutput;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.ec2.IVpc;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ec2.VpcLookupOptions;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.iam.ManagedPolicy;
import software.amazon.awscdk.services.iam.Role;

import java.util.Arrays;

public class CreateEcsTask extends Stack {

    public static final String CLUSTER_NAME = "Cluster Name";

    public CreateEcsTask(final Construct parent, final String name, StackProps props) {
        super(parent, name, props);

        final String vpcId = this.getNode().tryGetContext("vpc-id").toString();
        final String image = this.getNode().tryGetContext("image").toString();
        IVpc vpc = Vpc.fromLookup(this, "VPC-Lookup",
                new VpcLookupOptions.Builder().vpcId(vpcId).build());

        // Create VPC
        Cluster cluster = Cluster.Builder.create(this, "Cluster")
                .clusterName("Fargate-Job-Cluster")
                .vpc(vpc)
                .build();

        // Create task role
        Role taskRole = RoleUtils.createRole(this, "Ecs-Fargate-Task-Role", "ecs-tasks.amazonaws.com",
                Arrays.asList(
                        ManagedPolicy.fromAwsManagedPolicyName("AWSCodeCommitFullAccess"),
                        ManagedPolicy.fromAwsManagedPolicyName("AmazonEC2ContainerRegistryFullAccess"),
                        ManagedPolicy.fromAwsManagedPolicyName("AmazonSNSFullAccess")
                )
        );

        // Create Job Execution role
        Role jobExecutionRole = RoleUtils.createRole(this, "Ecs-Fargate-Task-Execution-Role", "ecs-tasks.amazonaws.com",
                Arrays.asList(
                        ManagedPolicy.fromAwsManagedPolicyName("AmazonS3FullAccess"),
                        ManagedPolicy.fromAwsManagedPolicyName("CloudWatchFullAccess"),
                        ManagedPolicy.fromAwsManagedPolicyName("AmazonEC2ContainerRegistryFullAccess")
                )
        );

        FargateTaskDefinition fargateTaskDefinition = FargateTaskDefinition.Builder.create(this, "TaskDef")
                .memoryLimitMiB(4096)
                .cpu(2048)
                .executionRole(jobExecutionRole)
                .taskRole(taskRole)
                .build();

        ContainerDefinition container = fargateTaskDefinition.addContainer("Fargate-Container", new ContainerDefinitionOptions.Builder().image(new ContainerImage() {
            @Override
            public @NotNull ContainerImageConfig bind(@NotNull Construct construct, @NotNull ContainerDefinition containerDefinition) {
                return () -> image;
            }
        }).logging(new AwsLogDriver(fargateTaskDefinition::getFamily)).build());

        CfnOutput.Builder.create(this, Constants.CLUSTER_NAME).exportName(Constants.CLUSTER_NAME).value(cluster.getClusterName()).build();
        CfnOutput.Builder.create(this, Constants.CLUSTER_ARN).exportName(Constants.CLUSTER_ARN).value(cluster.getClusterArn()).build();
        CfnOutput.Builder.create(this, Constants.TASK_ARN).exportName(Constants.TASK_ARN).value(fargateTaskDefinition.getTaskDefinitionArn()).
                build();
        CfnOutput.Builder.create(this, Constants.CONTAINER_ARN).exportName(Constants.CONTAINER_ARN).value(container.getContainerName()).
                build();
        CfnOutput.Builder.create(this, Constants.TASK_EXECUTION_ROLE).exportName(Constants.TASK_EXECUTION_ROLE).value(jobExecutionRole.getRoleArn()).
                build();
        CfnOutput.Builder.create(this, Constants.TASK_ROLE).exportName(Constants.TASK_ROLE).value(taskRole.getRoleArn()).
                build();

    }
}

