package com.amazon.awscdk.stack;

import com.amazon.cdk.Constants;
import software.amazon.awscdk.core.CfnOutput;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.services.ec2.ISubnet;
import software.amazon.awscdk.services.ec2.IVpc;
import software.amazon.awscdk.services.ec2.Vpc;

import java.util.stream.Collectors;

public class CdkCreateVpc extends Stack {

    public CdkCreateVpc(final Construct parent, final String name) {
        super(parent, name);

        // Create VPC
        IVpc vpc = Vpc.Builder.create(this, "Batch-VPC").build();

        // Output
        CfnOutput.Builder.create(this, Constants.VPC_ID).exportName(Constants.VPC_ID).value(vpc.getVpcId()).build();
        CfnOutput.Builder.create(this, Constants.PRIVATE_SUBNET).exportName(Constants.PRIVATE_SUBNET)
                .value(vpc.getPrivateSubnets().stream().map(ISubnet::getSubnetId).collect(Collectors.joining(", "))).build();
        CfnOutput.Builder.create(this, Constants.PUBLIC_SUBNET).exportName(Constants.PUBLIC_SUBNET)
                .value(vpc.getPublicSubnets().stream().map(ISubnet::getSubnetId).collect(Collectors.joining(", "))).build();
    }
}
