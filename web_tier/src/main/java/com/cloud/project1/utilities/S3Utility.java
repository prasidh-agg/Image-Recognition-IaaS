package com.cloud.project1.utilities;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.cloud.project1.configs.AWSProperties;
import com.cloud.project1.configs.AWSSecrets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;


@Component
public class S3Utility {

    private static final Logger logger = LoggerFactory.getLogger(S3Utility.class);
    private final AmazonS3 s3;
    private final AWSProperties awsProperties;
    BasicAWSCredentials AWS_CREDENTIALS = AWSSecrets.getAWSCredentials();

    @Autowired
    public S3Utility(AWSProperties awsProperties) {
        this.awsProperties = awsProperties;
        s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(AWS_CREDENTIALS))
                .withRegion(Regions.US_EAST_1)
                .build();
    }

    /**
     * * Upload an image to s3 bucket (Requests)
     * @param keyName Name of the file to be uploaded
     * @param uploadedImageFile File to be uploaded
     * @throws IOException S3 exceptions
     */
    public void saveImageToS3(String keyName, MultipartFile uploadedImageFile) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(uploadedImageFile.getSize());
        metadata.setContentType(uploadedImageFile.getContentType());
        InputStream is = uploadedImageFile.getInputStream();
        s3.putObject(awsProperties.getS3ImagesBucket(), keyName, is, metadata);
        logger.info("uploaded {} to S3..", keyName);
    }
}
