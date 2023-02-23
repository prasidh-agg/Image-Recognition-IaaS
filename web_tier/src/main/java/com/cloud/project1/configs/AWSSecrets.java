package com.cloud.project1.configs;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

@Component
public class AWSSecrets {

    private static final Logger logger= LoggerFactory.getLogger(AWSSecrets.class);

    public static BasicAWSCredentials getAWSCredentials() {
        AWSSimpleSystemsManagement ssmClient = AWSSimpleSystemsManagementClientBuilder.standard().build();
        GetParameterRequest parameterRequest = new GetParameterRequest()
                .withName("/project-1/credentials")
                .withWithDecryption(true);

        GetParameterResult parameterResult = ssmClient.getParameter(parameterRequest);
        String parameterValue = parameterResult.getParameter().getValue();
        Properties props = new Properties();
        try {
            props.load(new StringReader(parameterValue));
        } catch (IOException e) {
            logger.error("Exception is as follows : {}", e.toString());
        }
        String accessKey = props.getProperty("access_key");
        String secretKey = props.getProperty("secret_key");
        return new BasicAWSCredentials(accessKey, secretKey);

    }

}
