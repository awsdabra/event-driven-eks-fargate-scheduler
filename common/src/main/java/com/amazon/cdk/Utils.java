package com.amazon.cdk;

import software.amazon.awscdk.core.Environment;

public class Utils {
    public static Environment makeEnv(String account, String region) {
        return Environment.builder()
                .account(account)
                .region(region)
                .build();
    }
}
