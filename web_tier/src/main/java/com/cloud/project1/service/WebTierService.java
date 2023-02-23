package com.cloud.project1.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface WebTierService {
    String handleFileUpload(MultipartFile file) throws IOException, InterruptedException;

}
