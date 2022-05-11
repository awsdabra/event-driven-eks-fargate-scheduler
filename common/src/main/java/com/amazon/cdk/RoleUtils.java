package com.amazon.cdk;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.services.iam.IManagedPolicy;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;

import java.util.List;

public class RoleUtils {
    public static Role createRole(Construct construct, String name, String principal, List<IManagedPolicy> managedPolicyList) {
        Role.Builder roleBuilder = Role.Builder.create(construct, name);
        roleBuilder.assumedBy(new ServicePrincipal(principal));
        roleBuilder.roleName(name);
        Role role = roleBuilder.build();
        managedPolicyList.forEach(role::addManagedPolicy);
        return role;
    }
}
