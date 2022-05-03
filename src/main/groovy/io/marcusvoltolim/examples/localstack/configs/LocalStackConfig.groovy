package io.marcusvoltolim.examples.localstack.configs

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition
import software.amazon.awssdk.services.dynamodb.model.BillingMode
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement
import software.amazon.awssdk.services.dynamodb.model.KeyType
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType
import software.amazon.awssdk.services.sqs.SqsClient

import javax.annotation.PostConstruct

@Profile('localstack')
@Configuration
class LocalStackConfig {

    final DynamoDbClient dynamoDbClient
    final SqsClient sqsClient
    private final List<String> queuesNames
    private final List<String> tablesNames

    LocalStackConfig(DynamoDbClient dynamoDbClient,
                     SqsClient sqsClient,
                     @Value('${application.sqs.queues.names:}') List<String> queuesNames,
                     @Value('${application.dynamo.tables.names:}') List<String> tablesNames) {
        this.dynamoDbClient = dynamoDbClient
        this.sqsClient = sqsClient
        this.queuesNames = queuesNames
        this.tablesNames = tablesNames
    }

    @PostConstruct
    void init() {
        initDynamo()
        initSqs()
    }

    void initDynamo() {
        tablesNames.each { name ->
            try {
                dynamoDbClient.createTable {
                    it.tableName(name)
                        .billingMode(BillingMode.PAY_PER_REQUEST)
                        .attributeDefinitions(AttributeDefinition.builder().attributeName("Id").attributeType(ScalarAttributeType.S).build())
                        .keySchema(KeySchemaElement.builder().attributeName("Id").keyType(KeyType.HASH).build())
                }
            } catch (ResourceInUseException ignored) {
                //tabela ja existe, ignorar excecão pra simplificar.
            }
        }
    }

    void initSqs() {
        queuesNames.forEach(name -> sqsClient.createQueue {
            it.queueName(name)
        })
    }

}
