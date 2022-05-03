package io.marcusvoltolim.examples.localstack.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.SqsResponse

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@RequestMapping('/sqs')
@RestController
class SqsController {

    private final SqsClient sqsClient
    private final ObjectMapper objectMapper

    SqsController(SqsClient sqsClient, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient
        this.objectMapper = objectMapper
    }

    @GetMapping(path = '/list-queues', produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Map> allMessages() {
        sqsClient.listQueues()
            .with {
                convertResponse(it)
            }
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Map> sendMessage(@RequestParam String queueName, @RequestBody String json) {
        sqsClient.sendMessage { it.queueUrl(getQueueUrl(queueName)).messageBody(json) }
            .with {
                convertResponse(it)
            }
    }

    private ResponseEntity<Map> convertResponse(SqsResponse result) {
        ResponseEntity.ok(objectMapper.convertValue(result.toBuilder(), Map))
    }

    private String getQueueUrl(String queueName) {
        sqsClient.getQueueUrl() { it.queueName(queueName) }.queueUrl()
    }

}
