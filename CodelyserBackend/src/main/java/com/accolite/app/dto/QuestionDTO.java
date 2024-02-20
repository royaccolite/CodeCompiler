package com.accolite.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class QuestionDTO {
    private Long id;
    private String title;
    private String description;
    private List<TemplateDTO> templates;
    private List<TestCaseDTO> testCases;
}
