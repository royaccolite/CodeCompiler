package com.accolite.app.implementation;

import com.accolite.app.compiler.*;
import com.accolite.app.dto.CodeDTO;
import com.accolite.app.entity.TestCase;
import com.accolite.app.entity.TestCaseOutput;
import com.accolite.app.entity.TestSubmission;
import com.accolite.app.exception.QuestionIDNotFoundException;
import com.accolite.app.exception.TestCaseNotFoundException;
import com.accolite.app.repository.QuestionRepository;
import com.accolite.app.repository.TestCaseOutputRepository;
import com.accolite.app.repository.TestCaseRepository;
import com.accolite.app.repository.TestSubmissionRepository;
import com.accolite.app.service.CodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class CodeServiceImpl implements CodeService {
    @Autowired
    TestCaseRepository testCaseRepository;

    @Autowired
    TestCaseOutputRepository testCaseOutputRepository;
    @Autowired
    TestSubmissionRepository testSubmissionRepository;
    @Autowired
    QuestionRepository questionRepository;


    // Base directory will be created based on the PWD
    private final String BASE_DIR = Paths.get(System.getProperty("user.dir")) + "\\";

    List<String> results = new ArrayList<>();

    @Override
    public List<String> submitCode(CodeDTO code) throws IOException, InterruptedException {
        results.clear();// to clear the results each time

        if (!testCaseRepository.existsByQuestionId(code.getQuestionId())) {
            throw new QuestionIDNotFoundException("Question ID not found: " + code.getQuestionId());
        }
        List<TestCase> testCases = new ArrayList<>();
        testCaseRepository.findAllByQuestionId(code.getQuestionId()).forEach(testCases::add);
        if (testCases.isEmpty()) {
            throw new TestCaseNotFoundException("No test cases found for question ID: " + code.getQuestionId());
        }
        switch (code.getLanguage()) {
            case "java" -> {
                JavaCodeCompiler compiler = new JavaCodeCompiler();
                CompletableFuture<CompilationResult> compilationFuture = CompletableFuture.supplyAsync(() -> compiler.compile(code.getSubmittedCode()));
                try {
                    CompilationResult compilationResult = compilationFuture.get(questionRepository.findById(code.getQuestionId()).get().getCompilationTimeout(), TimeUnit.SECONDS);

                    // checking if the code runs without compilation error
                    if (compilationResult.isSuccess()) {
                        results.add("Compilation Successful");

                        int temp = 0;

                        TestSubmission testSubmission = addSubmissionDetails(code, true);
                        String output;
                        String generatedClassName = compiler.extractClassName(code.getSubmittedCode());

                        if (code.getCustomInput() != null && !code.isSubmitStatus()) {
                            output = compiler.runCompiledClass(generatedClassName, code.getCustomInput().split(" "));
                            results.add("Output: " + output);
                        } else {
                            int timeLimit = 4000;
                            for (TestCase testCase : testCases) {
                                String[] input = testCase.getInput().split(" ");
                                //executing the code
                                try {
                                    CompletableFuture<String> executionFuture = CompletableFuture.supplyAsync(() -> compiler.runCompiledClass(generatedClassName, input));
                                    output = executionFuture.get(timeLimit, TimeUnit.MILLISECONDS);
                                    //Function to check if the test cases and output are matching
                                    String checkedOutput = removeLinesStartingWithPrefix(output);
                                    checkTestCase(testCase, temp, checkedOutput, code, testSubmission,output);
                                    //incrementing temp to show only 3 testcases
                                    temp++;
                                    timeLimit -= 50;
                                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                                    saveOutputs("Time Limit Exceeded", testCase.getOutput(), false, code, testSubmission);
                                    results.add("Time Limit Exceeded");
                                }
                            }
                        }
                    } else {
                        results.add("Compilation Failed " + compilationResult.getErrorDetails());
                        addSubmissionDetails(code, false);
                    }
                } catch (ExecutionException | TimeoutException e) {
                    addSubmissionDetails(code, false);
                    results.add("Compilation Timed out");
                }
            }
            // cpp compiler
            case "cpp" -> {
                Path filepath = createCandidateDirectory(code.getCandidateId());

                CppCodeCompiler cppCompiler = new CppCodeCompiler();
                try {
                    CompletableFuture<CompilationResult> compilationFuture = CompletableFuture.supplyAsync(() -> {
                        try {
                            return cppCompiler.compileCpp(code, filepath);
                        } catch (IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });


                    CompilationResult compilationResult = compilationFuture.get(questionRepository.findById(code.getQuestionId()).get().getCompilationTimeout(), TimeUnit.SECONDS);
                    if (compilationResult.isSuccess()) {
                        results.add("Compilation Successful");

                        int temp = 0;
                        TestSubmission testSubmission = addSubmissionDetails(code, true);
                        String output;
                        String programFileName = "Program" + code.getCandidateId() + "." + code.getQuestionId();
                        if (code.getCustomInput() != null && !code.isSubmitStatus()) {
                            output = executeCppCode(cppCompiler, code.getCustomInput(), filepath, programFileName);
                            results.add("Output: " + output);
                        } else {
                            int timeLimit = 1000;
                            for (TestCase testCase : testCases) {
                                try {
                                    CompletableFuture<String> executionFuture = CompletableFuture.supplyAsync(() -> executeCppCode(cppCompiler, testCase.getInput(), filepath, programFileName));
                                    output = executionFuture.get(timeLimit, TimeUnit.MILLISECONDS);
                                    String checkedOutput = removeLinesStartingWithPrefix(output);
                                    checkTestCase(testCase, temp, checkedOutput, code, testSubmission,output);
                                    temp++;
                                    timeLimit -= 50;
                                } catch (ExecutionException | TimeoutException e) {
                                    saveOutputs("Time Limit Exceeded", testCase.getOutput(), false, code, testSubmission);
                                    results.add("Time Limit Exceeded");
                                }
                            }
                        }
                    } else {
                        results.add("Compilation Failed " + compilationResult.getErrorDetails());
                        addSubmissionDetails(code, false);
                    }
                } catch (ExecutionException | TimeoutException e) {
                    addSubmissionDetails(code, false);
                    results.add("Compilation Timed out");
                }
            }
            // python Interpreter
            case "python" -> {
                Path filepath = createCandidateDirectory(code.getCandidateId());

                PythonCodeInterpreter pythonCompiler = new PythonCodeInterpreter();
                // issue is here as i hardcoded it will give error
                try {
                    CompletableFuture<ExecutionResult> completableFuture = CompletableFuture.supplyAsync(() -> runPythonCode(code, pythonCompiler, testCaseRepository.findById(code.getQuestionId()).get().getInput(), filepath));
                    ExecutionResult executionResult = completableFuture.get(questionRepository.findById(code.getQuestionId()).get().getCompilationTimeout(), TimeUnit.MILLISECONDS);// initializing with hardcoded input
                    // to get the object
                    // and check the errors
                    if (executionResult.isSuccess()) {
                        results.add("Execution Successful");

                        int temp = 0;
                        TestSubmission testSubmission = addSubmissionDetails(code, true);
                        String output;

                        if (code.getCustomInput() != null && !code.isSubmitStatus()) {
                            output = pythonCompiler.executePython(code, filepath, code.getCustomInput()).getOutput();
                            results.add("Output: " + output);
                        } else {
                            int timeLimit = 1000;
                            for (TestCase testCase : testCases) {
                                try {
                                    CompletableFuture<ExecutionResult> testFuture = CompletableFuture.supplyAsync(() -> runPythonCode(code, pythonCompiler, testCase.getInput(), filepath));
                                    output = testFuture.get(timeLimit, TimeUnit.MILLISECONDS).getOutput();
                                    checkTestCase(testCase, temp, output, code, testSubmission,output);
                                    temp++;
                                    timeLimit -= 200;
                                } catch (ExecutionException | TimeoutException e) {
                                    saveOutputs("Time Limit Exceeded", testCase.getOutput(), false, code, testSubmission);
                                    results.add("Time Limit Exceeded");
                                }
                            }
                        }
                    } else {
                        results.add("Execution Failed " + executionResult.getErrors());
                        addSubmissionDetails(code, false);
                    }
                } catch (ExecutionException | TimeoutException e) {
                    addSubmissionDetails(code, false);
                    results.add("Compilation Timed out");
                }
            }
            default -> results.add("Unsupported Language");
        }
        return results;
    }

    // used for checking testcase
    private void checkTestCase(TestCase test, int temp, String output, CodeDTO code, TestSubmission testSubmission,String originalOutput) {
        int flag = temp+1;
        if (test.getOutput().equals(output)) {
            if (temp < 1) {
                results.add("Output: " + originalOutput);
            }
            results.add("Test Case "+flag+" Passed");
            saveOutputs(output, test.getOutput(), true, code, testSubmission);

        } else {
            if (temp < 2) {
                results.add("Test Case "+flag+" Failed");
                results.add("Your Output: " + output);
                results.add("Expected Output: " + test.getOutput());
            } else {
                results.add("Test Case "+flag+" Failed");
            }
            saveOutputs(output, test.getOutput(), false, code, testSubmission);
        }
    }

    // adds data to submission table is its submitting
    private TestSubmission addSubmissionDetails(CodeDTO code, Boolean status) {
        if (code.isSubmitStatus()) {
            return testSubmissionRepository.save(new TestSubmission(code.getCandidateId(), code.getQuestionId(), code.getSubmittedCode(), status));
        }
        return null;
    }

    // saveOutput will save the data to a database if the status is true
    private void saveOutputs(String actualOutput, String expectedOutput, Boolean exitValue, CodeDTO code, TestSubmission testSubmission) {
        if (code.isSubmitStatus()) {
            TestCaseOutput testCaseOutput = new TestCaseOutput(actualOutput, expectedOutput, exitValue);

            testCaseOutput.setTestSubmission(testSubmission);
            testCaseOutputRepository.save(testCaseOutput);

            testSubmission.getTestCaseOutputs().add(testCaseOutput);
            testSubmissionRepository.save(testSubmission);
        }
    }

    // runs the cpp code created for custom test cases
    private String executeCppCode(CppCodeCompiler cppCompiler, String input, Path filepath, String programFileName) {
        try {
            return cppCompiler.executeCpp(input, filepath, programFileName);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private ExecutionResult runPythonCode(CodeDTO code, PythonCodeInterpreter pythonCompiler, String input, Path filepath) {
        try {
            return pythonCompiler.executePython(code, filepath, input);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // creates a folder based on candidateID, and the files inside will be created based on a candidate and questionId
    private Path createCandidateDirectory(Long candidateID) {
        Path folderPath = Paths.get(BASE_DIR + "Candidate" + candidateID);
        try {
            // Delete the directory forcefully if it exists
            if (Files.exists(folderPath)) {
                deleteDirectory(folderPath);
            }
            // Create the directory
            Files.createDirectories(folderPath);

        } catch (IOException e) {
            throw new RuntimeException("Error creating candidate directory: " + e.getMessage(), e);
        }
        return folderPath;
    }

    public static void deleteDirectory(Path directory) throws IOException {
        Files.walkFileTree(directory, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                // Handle the error, or ignore it
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc == null) {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                } else {
                    throw exc;
                }
            }
        });
    }

    private String removeLinesStartingWithPrefix(String output) {
        String[] lines = output.split("\\n");
        List<String> filteredOutput = new ArrayList<>();
        for (String line : lines) {
            if (!line.startsWith("##")) {
                filteredOutput.add(line);
            }
        }
        return String.join("\n", filteredOutput);
    }
}