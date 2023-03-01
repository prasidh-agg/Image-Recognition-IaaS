package com.cloud.project1.controller;

import com.cloud.project1.service.WebTierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/")
public class WebTierRestController {

    @Autowired
    WebTierService webTierService;

    /**
     * *Handles the file upload, including upload to s3 as well as SQS
     * @param file File to be uploaded
     * @return Classification result of the uploaded image
     * @throws IOException IO Exceptions
     * @throws InterruptedException Interrupted Exceptions
     */
    @PostMapping(value = "/file-upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException, InterruptedException {
        return webTierService.handleFileUpload(file);
    }


}
