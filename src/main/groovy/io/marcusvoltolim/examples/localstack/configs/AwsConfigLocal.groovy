package io.marcusvoltolim.examples.localstack.configs

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.sqs.SqsClient

@Profile('localstack')
@Configuration
class AwsConfigLocal {

    private static final URI URI_LOCALSTACK = URI.create('http://localhost:4566')
    private static final StaticCredentialsProvider STATIC_CREDENTIALS = StaticCredentialsProvider.create(AwsBasicCredentials.create('none', 'none'))

    @Bean
    DynamoDbClient dynamoDbEnhancedClient() {
        DynamoDbClient.builder()
            .credentialsProvider(STATIC_CREDENTIALS)
            .endpointOverride(URI_LOCALSTACK)
            .region(Region.SA_EAST_1)
            .build()
    }


    @Bean
    SqsClient sqsClient() {
        SqsClient.builder()
            .credentialsProvider(STATIC_CREDENTIALS)
            .endpointOverride(URI_LOCALSTACK)
            .region(Region.SA_EAST_1)
            .build()
    }


}
