package com.hcservice.domain.request;

import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@ToString
public class ReportRepairRequest {

    private String type;

    private String description;

    private MultipartFile[] multipartFiles;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MultipartFile[] getMultipartFiles() {
        return multipartFiles;
    }

    public void setMultipartFiles(MultipartFile[] multipartFiles) {
        this.multipartFiles = multipartFiles;
    }
}
