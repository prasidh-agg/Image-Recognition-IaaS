package com.cloud.project1.utilities;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.cloud.project1.configs.AWSProperties;
import com.cloud.project1.configs.AWSSecrets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.lang.Boolean.TRUE;

@Component
public class SQSUtility {

    private static final Logger logger = LoggerFactory.getLogger(SQSUtility.class);
    private final AmazonSQS amazonSQS;
    private final AWSProperties awsProperties;
    BasicAWSCredentials AWS_CREDENTIALS = AWSSecrets.getAWSCredentials();

    @Autowired
    public SQSUtility(AWSProperties awsProperties) {
        this.awsProperties = awsProperties;
        amazonSQS = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(AWS_CREDENTIALS))
                .withRegion(Regions.US_EAST_1)
                .build();
    }

    /**
     * Send the message to the SQS request queue with file name as the body
     * @param message File/image name
     */
    public void sendMsgToRequestQueue(String message) {
        SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl(awsProperties.getSqsRequestUrl())
                .withMessageBody(message);
        amazonSQS.sendMessage(sendMessageRequest);
        logger.info("sent {} to request SQS..", message);
    }

    /**
     * * Poll the SQS response queue and read messages from it, while storing it in a global map.
     * @param resultMap Map of file/image names and their classification results
     */
    public void readMessages(Map<String, String> resultMap) {
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest()
                .withQueueUrl(awsProperties.getSqsResponseUrl())
                .withAttributeNames("All")
                .withMessageAttributeNames("All")
                .withMaxNumberOfMessages(10)
                .withWaitTimeSeconds(5);

        while (TRUE) {
            try {
                ReceiveMessageResult result = amazonSQS.receiveMessage(receiveMessageRequest);
                List<Message> messages = result.getMessages();
                for (Message message : messages) {
                    String[] parts;
                    parts = message.getBody().split(",");
                    String responseFileName = parts[0];
                    String classificationResult = parts[1];
                    logger.info("from response SQS: {} - {}", responseFileName, classificationResult);
                    amazonSQS.deleteMessage(awsProperties.getSqsResponseUrl(), message.getReceiptHandle());
                    logger.info("{} deleted from response queue..\n", responseFileName);
                    resultMap.put(responseFileName, classificationResult);
                }
            } catch (Exception e) {
                logger.error("Exception occurred.." + e.getMessage());
            }
        }
    }
}

