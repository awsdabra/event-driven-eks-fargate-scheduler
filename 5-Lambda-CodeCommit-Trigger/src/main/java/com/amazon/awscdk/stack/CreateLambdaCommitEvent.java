package com.amazon.awscdk.stack;

import com.amazon.cdk.RoleUtils;
import software.amazon.awscdk.core.*;
import software.amazon.awscdk.services.events.EventPattern;
import software.amazon.awscdk.services.events.IRuleTarget;
import software.amazon.awscdk.services.events.Rule;
import software.amazon.awscdk.services.events.targets.LambdaFunction;
import software.amazon.awscdk.services.iam.ManagedPolicy;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CreateLambdaCommitEvent extends Stack {

    public CreateLambdaCommitEvent(final Construct parent, final String name, StackProps props) {
        super(parent, name, props);

        // Create task role
        Role lambdaRole = RoleUtils.createRole(this, "Ecs-Fargate-Task-Lambda-Role", "lambda.amazonaws.com",
                Arrays.asList(
                        ManagedPolicy.fromAwsManagedPolicyName("AWSCodeCommitFullAccess"),
                        ManagedPolicy.fromAwsManagedPolicyName("CloudWatchFullAccess"),
                        ManagedPolicy.fromAwsManagedPolicyName("AmazonECS_FullAccess")
                )
        );

        // Lambda properties
        FunctionProps.Builder functionPropBuilder = new FunctionProps.Builder();
        functionPropBuilder.functionName("AWS-Code-Scanner-Function");
        Map<String, String> env = new HashMap<>();
        env.put("CLUSTER", "Fargate-Job-Cluster");
        env.put("CONTAINER_NAME", "Fargate-Container");
        env.put("RUNNER", this.getNode().tryGetContext("taskarn").toString());
        env.put("SNS_TOPIC", this.getNode().tryGetContext("snstopic").toString());
        env.put("SUBNET", this.getNode().tryGetContext("subnet").toString());
        functionPropBuilder.environment(env);
        functionPropBuilder.memorySize(1500);
        functionPropBuilder.timeout(Duration.minutes(5));
        functionPropBuilder.handler("index.handler");
        functionPropBuilder.runtime(Runtime.NODEJS_12_X);
        functionPropBuilder.role(lambdaRole);
        functionPropBuilder.code(Code.fromInline(com.amazon.awscdk.stack.Constants.LAMBDA_CODE));
        FunctionProps functionProps = functionPropBuilder.build();

        // Define cloudwatch rule
        Rule.Builder ruleBuilder = Rule.Builder.create(this, "rule");

        EventPattern.Builder eventPatternBuilder = new EventPattern.Builder();
        eventPatternBuilder.source(Collections.singletonList("aws.codecommit"));
        eventPatternBuilder.detailType(Collections.singletonList("CodeCommit Pull Request State Change"));
        eventPatternBuilder.resources(Collections.singletonList(this.getNode().tryGetContext("coderepo").toString()));

        ruleBuilder.eventPattern(eventPatternBuilder.build());
        ruleBuilder.enabled(true);

        // Rule target
        IFunction function = new Function(this, "function-prop", functionProps);
        IRuleTarget ruleTarget = new LambdaFunction(function);
        ruleBuilder.targets(Collections.singletonList(ruleTarget));
        Rule rule = ruleBuilder.build();

        CfnOutput.Builder.create(this, com.amazon.cdk.Constants.CODE_COMMIT_LAMBDA)
                .exportName(com.amazon.cdk.Constants.CODE_COMMIT_LAMBDA).value(functionProps.getFunctionName()).build();
        CfnOutput.Builder.create(this, com.amazon.cdk.Constants.LAMBDA_ROLE)
                .exportName(com.amazon.cdk.Constants.LAMBDA_ROLE).value(rule.getRuleArn()).build();
        CfnOutput.Builder.create(this, com.amazon.cdk.Constants.CLOUD_WATCH_RULE)
                .exportName(com.amazon.cdk.Constants.CLOUD_WATCH_RULE).value(rule.getRuleArn()).build();
    }
}

