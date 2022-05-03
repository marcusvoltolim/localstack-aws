package io.marcusvoltolim.examples.localstack.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@RequestMapping('/dynamo')
@RestController
class DynamoDbController {

    private final DynamoDbClient dynamoClient
    private final ObjectMapper objectMapper

    DynamoDbController(DynamoDbClient dynamoClient, ObjectMapper objectMapper) {
        this.dynamoClient = dynamoClient
        this.objectMapper = objectMapper
    }

    @GetMapping(path = '/list-tables', produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Map> allMessages() {
        dynamoClient.listTables()
            .with {
                ResponseEntity.ok(objectMapper.convertValue(it.toBuilder(), Map))
            }
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity putItem(@RequestParam String tableName, @RequestBody String json) {
        dynamoClient.putItem { it.tableName(tableName).item(buildItem(json)) }
        ResponseEntity.created(null).build()
    }

    private Map<String, AttributeValue> buildItem(String json) {
        objectMapper.readValue(json, Map).collectEntries {
            [it.key, AttributeValue.builder().s(it.value.toString()).build()]
        }
    }

}
