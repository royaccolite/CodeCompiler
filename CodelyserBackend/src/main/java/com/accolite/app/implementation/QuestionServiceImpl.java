package com.accolite.app.implementation;
import com.accolite.app.dto.QuestionDTO;
import com.accolite.app.dto.TemplateDTO;
import com.accolite.app.dto.TestCaseDTO;
import com.accolite.app.entity.*;
import com.accolite.app.exception.CandidateNotFoundException;
import com.accolite.app.repository.CandidateRepository;
import com.accolite.app.repository.TestResultRepository;
import com.accolite.app.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    CandidateRepository candidateRepository;

    @Autowired
    TestResultRepository testResultRepository;
    @Override
    public List<QuestionDTO> getQuestionsByCandidateId(Long candidateId) {

        // Checking a candidate with given candidateID is present or not
        Optional<Candidate> candidateOptional= candidateRepository.findById(candidateId);

        // The specified candidate ID is not present in the database
        if(candidateOptional.isEmpty()){
            throw new CandidateNotFoundException("Candidate Not Found");
        }
        // The specified candidate ID is present in the database
        else{
            Candidate candidate = candidateOptional.get();
            List<Question> questions = candidate.getQuestions();

            // creating test_result table entry with candidate ID
            Optional<TestResult> optionalTestResult= testResultRepository.findById(candidateId);
            if(optionalTestResult.isEmpty()){
                TestResult testResult=new TestResult();
                testResult.setId(candidateId);
                testResult.setStartTime(LocalDateTime.now());
                testResult.setStatus(0);
                testResultRepository.save(testResult);
            }

            //return QuestionDTOs of every candidate
            return questions.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
    }

    private QuestionDTO convertToDTO(Question question) {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setId(question.getId());
        questionDTO.setTitle(question.getTitle());
        questionDTO.setDescription(question.getDescription());
        questionDTO.setTestCases(convertTestCasesToDTOs(question.getTestCases()));
        // Assuming you have a method to convert templates to TemplateDTOs
        questionDTO.setTemplates(convertTemplatesToDTOs(question.getTemplates()));
        return questionDTO;
    }

    private List<TestCaseDTO> convertTestCasesToDTOs(List<TestCase> testCases) {
        return testCases.stream()
                .limit(2) // limit to only two test cases
                .map(testCase -> {
                    TestCaseDTO testCaseDTO = new TestCaseDTO();
                    testCaseDTO.setInput(testCase.getInput());
                    testCaseDTO.setOutput(testCase.getOutput());
                    return testCaseDTO;
                })
                .collect(Collectors.toList());
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
}