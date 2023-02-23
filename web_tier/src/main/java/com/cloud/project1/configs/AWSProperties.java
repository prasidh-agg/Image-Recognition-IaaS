package com.cloud.project1.configs;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class AWSProperties {

    @Value("${sqs_request_url}")
    private String sqsRequestUrl;

    @Value("${sqs_response_url}")
    private String sqsResponseUrl;

    @Value("${s3_images_bucket}")
    private String s3ImagesBucket;

    @Value("${s3_results_Bucket}")
    private String s3ResultsBucket;


}
