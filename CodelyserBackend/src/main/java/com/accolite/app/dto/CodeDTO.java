package com.accolite.app.dto;

import lombok.Data;

@Data
public class CodeDTO {
    private Long candidateId;
    private Long questionId;
    private String submittedCode;
    private String language;
    private String customInput;
    private boolean submitStatus;
}
