package com.amazon.awscdk.stack;

import com.amazon.cdk.Utils;
import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Environment;
import software.amazon.awscdk.core.StackProps;

public class CreateLambdaCommitEventApp {
    public static void main(final String[] args) {
        App app = new App();
        Environment env = Utils.makeEnv(System.getenv("CDK_DEFAULT_ACCOUNT"), System.getenv("CDK_DEFAULT_REGION"));
        new CreateLambdaCommitEvent(app, "aws-cdk-fargate-lambda-event", StackProps.builder().env(env).build());
        app.synth();
    }
}
