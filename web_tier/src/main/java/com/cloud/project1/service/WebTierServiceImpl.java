package com.cloud.project1.service;

import com.cloud.project1.utilities.S3Utility;
import com.cloud.project1.utilities.SQSUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebTierServiceImpl implements WebTierService{

    @Autowired
    S3Utility s3Utility;

    @Autowired
    SQSUtility sqsUtility;

    private static final Logger logger = LoggerFactory.getLogger(WebTierServiceImpl.class);

    Map<String, String> resultMap = new ConcurrentHashMap<>();

    /**
     * * Runs on application startup and polls the response queue
     */
    @PostConstruct
    public void receiveMessages() {
        CompletableFuture.runAsync(() -> sqsUtility.readMessages(resultMap));
    }

    /**
     * * Handles the file upload, including upload to s3 as well as SQS
     * @param file File to be uploaded
     * @return Classification result of the uploaded image
     * @throws IOException IO Exceptions
     * @throws InterruptedException Interrupted Exceptions
     */
    @Override
    public String handleFileUpload(MultipartFile file) throws IOException, InterruptedException {
        String imageName = file.getOriginalFilename();
        s3Utility.saveImageToS3(file.getOriginalFilename(),file);
        sqsUtility.sendMsgToRequestQueue(imageName);
        while (!resultMap.containsKey(imageName)) {
            Thread.sleep(5_000);
        }
        logger.info("results count: {}",resultMap.size());
        return resultMap.get(imageName);
        }

}
