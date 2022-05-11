package com.amazon.awscdk.stack;

public class Constants {
    public static String LAMBDA_CODE = "exports.handler = async(event) => {\n" +
            "    const AWS = require('aws-sdk');\n" +
            "    const ecs = new AWS.ECS();\n" +
            "    const { RUNNER, SUBNET, CLUSTER, CONTAINER_NAME, SNS_TOPIC } = process.env;\n" +
            "    if (event.detail.pullRequestStatus === \"Open\") {\n" +
            "        try {\n" +
            "            var splits = event.resources[0].split(\":\");\n" +
            "            var region = splits[3];\n" +
            "            var repo = splits[5];\n" +
            "            var repoUrl = \"https://git-codecommit.\" + region + \".amazonaws.com/v1/repos/\" + repo;\n" +
            "            var repoSplit = event.detail.sourceReference.split(\"/\");\n" +
            "            const runParams = {\n" +
            "                taskDefinition: RUNNER,\n" +
            "                cluster: CLUSTER,\n" +
            "                launchType: 'FARGATE',\n" +
            "                networkConfiguration: {\n" +
            "                    awsvpcConfiguration: {\n" +
            "                        assignPublicIp: 'ENABLED',\n" +
            "                        subnets: [SUBNET],\n" +
            "                    },\n" +
            "                },\n" +
            "                overrides: {\n" +
            "                    containerOverrides: [{\n" +
            "                        environment: [{\n" +
            "                            name: 'CODE_URL',\n" +
            "                            value: repoUrl,\n" +
            "                        }, {\n" +
            "                            name: 'SNS_TOPIC',\n" +
            "                            value: SNS_TOPIC,\n" +
            "                        }, {\n" +
            "                            name: 'BRANCH',\n" +
            "                            value: repoSplit[2],\n" +
            "                        }],\n" +
            "                        name: CONTAINER_NAME,\n" +
            "                    }]\n" +
            "                }\n" +
            "            };\n" +
            "\n" +
            "            const result = await ecs.runTask(runParams).promise();\n" +
            "            console.log(result);\n" +
            "        }\n" +
            "        catch (e) {\n" +
            "            console.log(e);\n" +
            "        }\n" +
            "    }\n" +
            "};\n";
}
