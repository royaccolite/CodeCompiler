package com.accolite.app.compiler;

import com.accolite.app.dto.CodeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.nio.file.*;


public class CppCodeCompiler {
    private static final Logger logger = LoggerFactory.getLogger(CppCodeCompiler.class);
    public CompilationResult compileCpp(CodeDTO code, Path folderPath) throws IOException, InterruptedException {
        // Write the C++ code to a temporary file
        String input = code.getSubmittedCode();
        String programFileName = "Program" + code.getCandidateId() + "." + code.getQuestionId();
        Path programDirectory = folderPath.resolve(programFileName);
        Path filePath = programDirectory.resolve(programFileName + ".cpp");

        // Ensure that the target directory exists
        Files.createDirectories(programDirectory);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toString()))) {
            writer.write(input);
        } catch (IOException e) {
            logger.error("An error Occurred: ", e);
            return new CompilationResult(false, "Error writing code to file: " + e.getMessage());
        }

        // Compile the C++ code using g++

        ProcessBuilder processBuilder = new ProcessBuilder("g++", filePath.toString(), "-o", programDirectory.resolve(programFileName + "_cpp_executable").toString());

        processBuilder.redirectErrorStream(true);  // Redirect the error stream to the input stream

        Process compileProcess = processBuilder.start();
        compileProcess.waitFor();

        // Capture the output and error streams
        InputStream inputStream = compileProcess.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder output = new StringBuilder();

        // Read the output of the compilation process
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        if (!output.isEmpty()) {
            output.replace(output.length()-1, output.length(), "");
        }

        // Check the exit value to determine success or failure
        int exitValue = compileProcess.exitValue();
        if (exitValue == 0) {
            return new CompilationResult(true, null);
        } else {
            return new CompilationResult(false, output.toString());
        }
    }

    public String executeCpp(String input, Path candidateDirectory, String programFileName) throws IOException, InterruptedException {
        // Execute the compiled C++ program
        Path programDirectory = candidateDirectory.resolve(programFileName);
        String executablePath = programDirectory.resolve(programFileName + "_cpp_executable").toString();

        ProcessBuilder processBuilder = new ProcessBuilder(executablePath);
        processBuilder.directory(programDirectory.toFile());

        Process executeProcess = processBuilder.start();

        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(executeProcess.getOutputStream())))) {
            writer.println(input);
        }

        // Read the output of the C++ program
        InputStream inputDataStream = executeProcess.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputDataStream));

        StringBuilder output = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        // removing the last one newline character to match with testcases
        output.replace(output.length() - 1, output.length(), "");

        return output.toString();
    }
}
