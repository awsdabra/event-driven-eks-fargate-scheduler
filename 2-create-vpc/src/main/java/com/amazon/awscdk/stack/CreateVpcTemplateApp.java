package com.amazon.awscdk.stack;

import software.amazon.awscdk.core.App;

public class CreateVpcTemplateApp {
    public static void main(final String[] args) {
        App app = new App();
        new CdkCreateVpc(app, "aws-batch-cdk-vpc-efs-launch-template");
        app.synth();
    }
}
