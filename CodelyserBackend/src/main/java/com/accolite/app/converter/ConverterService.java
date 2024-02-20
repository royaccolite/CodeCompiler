package com.accolite.app.converter;

import com.accolite.app.dto.*;
import com.accolite.app.entity.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConverterService {
    public Question convertQuestionToEntity(AdminQuestionDTO questionDTO) {
        Question question = new Question();
        question.setDescription(questionDTO.getDescription());
        question.setWeightage(questionDTO.getWeightage());
        question.setTitle(questionDTO.getTitle());
        question.setType(questionDTO.getType());
        return question;
    }

    public List<TestCase> convertTestcasesToEntity(List<TestCaseDTO> testcases, Question question) {
        return testcases.stream()
                .map(dto -> {
                    TestCase testCase = new TestCase();
                    testCase.setInput(dto.getInput());
                    testCase.setOutput(dto.getOutput());
                    testCase.setQuestion(question);
                    return testCase;
                })
                .collect(Collectors.toList());
    }

    public List<Template> convertTemplatesToEntity(List<TemplateDTO> templates, Question question) {
        return templates.stream()
                .map(dto -> {
                    Template template = new Template();
                    template.setCode(dto.getCode());
                    template.setLanguage(dto.getLanguage());
                    template.setQuestion(question);
                    return template;
                })
                .collect(Collectors.toList());
    }

    public AdminQuestionDTO convertQuestionToDTO(Question question) {
        AdminQuestionDTO dto = new AdminQuestionDTO();
        dto.setId(question.getId());
        dto.setTitle(question.getTitle());
        dto.setDescription(question.getDescription());
        dto.setWeightage(question.getWeightage());
        dto.setCompilationTimeout(question.getCompilationTimeout());
        dto.setType(question.getType());
        if(question.getType().equals("Coding")) {
            dto.setTemplates(convertTemplatesToDTOs(question.getTemplates()));
            dto.setTestcases(convertTestCasesToDTOs(question.getTestCases()));
        }
        else {
            dto.setQuery(question.getQuery());
            dto.setCommands(question.getCommands());
        }
        return dto;
    }

    private List<TemplateDTO> convertTemplatesToDTOs(List<Template> templates) {
        return templates.stream()
                .map(template -> {
                    TemplateDTO templateDTO = new TemplateDTO();
                    templateDTO.setCode(template.getCode());
                    templateDTO.setLanguage(template.getLanguage());
                    return templateDTO;
                })
                .collect(Collectors.toList());
    }

    private List<TestCaseDTO> convertTestCasesToDTOs(List<TestCase> testCases) {
        return testCases.stream()
                .map(testCase -> {
                    TestCaseDTO testCaseDTO = new TestCaseDTO();
                    testCaseDTO.setInput(testCase.getInput());
                    testCaseDTO.setOutput(testCase.getOutput());
                    return testCaseDTO;
                })
                .collect(Collectors.toList());
    }

    public Candidate convertCandidateToEntity(CandidateDTO candidateDTO, List<Question> questions) {
        Candidate candidate = new Candidate();
        candidate.setEmail(candidateDTO.getEmail());
        candidate.setPassword(candidateDTO.getPassword());
        candidate.setQuestions(questions);
        return candidate;
    }

    public CandidateDTO convertCandidateToDTO(Candidate candidate, TestResult result) {
        CandidateDTO dto = new CandidateDTO();
        dto.setEmail(candidate.getEmail());
        dto.setPassword(candidate.getPassword());
        if(result==null)
            dto.setStatus(400);
        else
            dto.setStatus(result.getStatus());
        List<AdminQuestionDTO> questions = new ArrayList<>();
        for (Question question : candidate.getQuestions()) {
            questions.add(convertQuestionToDTO(question));
        }
        dto.setQuestions(questions);
        return dto;
    }
}
