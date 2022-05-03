package io.marcusvoltolim.examples.localstack.configs

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.sqs.SqsClient

@Profile('!localstack')
@Configuration
class AwsConfig {

    private static final AwsCredentialsProvider AWS_CREDENTIALS

    static {
        try {
            AWS_CREDENTIALS = DefaultCredentialsProvider.create().tap { it.resolveCredentials() }
        } catch (SdkClientException ignored) {
            AWS_CREDENTIALS = ProfileCredentialsProvider.create()
        }
    }


    private final Region region

    AwsConfig(@Value('${application.aws-region}') String region) {
        this.region = Optional.ofNullable(region).map(Region::of).orElse(Region.SA_EAST_1)
    }

    @Bean
    DynamoDbClient dynamoDbEnhancedClient() {
        DynamoDbClient.builder().region(region).build()
    }


    @Bean
    SqsClient sqsClient() {
        SqsClient.builder().region(region).build()
    }

}
