package com.accolite.app.implementation;

import com.accolite.app.entity.TestCaseOutput;
import com.accolite.app.entity.Question;
import com.accolite.app.entity.TestResult;
import com.accolite.app.entity.TestSubmission;
import com.accolite.app.exception.QuestionIDNotFoundException;
import com.accolite.app.exception.TestResutNotFoundException;
import com.accolite.app.repository.QuestionRepository;
import com.accolite.app.repository.TestResultRepository;
import com.accolite.app.repository.TestSubmissionRepository;
import com.accolite.app.service.TestResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.accolite.app.exception.CandidateNotFoundException;

@Service
public class TestResultServiceImpl implements TestResultService {
    @Autowired
    TestSubmissionRepository submissionRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    TestResultRepository testResultRepository;
    @Override
    public void endTest(Long candidateId) {
        Optional<List<TestSubmission>> testSubmissionsOptional= Optional.ofNullable(submissionRepository.findByCandidateId(candidateId));
        int score=0;
        // The specified candidate ID is not present in the database
        if(!testSubmissionsOptional.isPresent())
        {
            score=0;
        }
        // The specified candidate ID is present in the database
        else
        {
            List<TestSubmission> testSubmissions =testSubmissionsOptional.get();
            for (TestSubmission testSubmission : testSubmissions)
            {
                Question question = Optional.ofNullable(testSubmission)
                        .map(TestSubmission::getQuestionId)
                        .flatMap(questionRepository::findById)
                        .orElse(null);

                if (question == null) {
                    throw new QuestionIDNotFoundException("Question ID not found: " + testSubmission.getQuestionId());
                }
                int weightage = Optional.ofNullable(question.getWeightage())
                        .orElse(0);
                if(question.getType().equals("database")){
                    if(testSubmission.isSubmitStatus()){
                        score+=weightage;
                    }
                }
                else{

                    int totalNumberOfTestCases = getNumberOfTestCases(question);;

                    int passedTestCases=0;
                    // Calculate the number of test cases passed for each submission if compilation is successful
                    if(testSubmission.isSubmitStatus()){
                        passedTestCases = (int) testSubmission.getTestCaseOutputs()
                                .stream()
                                .filter(TestCaseOutput::getExitValue)
                                .count();
                    }
                    score+= calculateScore(weightage, totalNumberOfTestCases, passedTestCases);

                }

            }

        }
        Optional<TestResult> testResultOptional = testResultRepository.findById(candidateId);
        if (testResultOptional.isEmpty()){
            throw new TestResutNotFoundException("Test Result Not found for ID: "+candidateId);
        }
        TestResult testResult= testResultOptional.get();
        testResult.setScore(score);
        testResult.setEndTime(LocalDateTime.now());
        testResult.setStatus(1);
        testResultRepository.save(testResult);
    }

    public int getNumberOfTestCases(Question question) {
        // Use streams to get the Question entity

        // Get the count of test cases and weightage from the Question entity
        int numberOfTestCases = Optional.ofNullable(question.getTestCases())
                .map(List::size)
                .orElse(0);

        return numberOfTestCases;
    }

    public int calculateScore(int weightage, int totalTestCases, int passedTestCases) {
        // If no test cases passed, return 0
        if (passedTestCases == 0) {
            return 0;
        }
        // Calculate the score based on the weightage, total test cases, and passed test cases
        double weightagePercentage = weightage / 100.0;  // Convert weightage to percentage
        double testCasePercentage = (double) passedTestCases / totalTestCases;

        // Calculate the final score as the product of weightage percentage and test case percentage
        double score = weightagePercentage * testCasePercentage * 100;

        // Round the score to the nearest integer
        return (int) Math.round(score);
    }

}