package com.accolite.app.compiler;
import com.accolite.app.dto.CodeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class PythonCodeInterpreter {
    private static final Logger logger = LoggerFactory.getLogger(PythonCodeInterpreter.class);
    private static final String pythonExecutable = "C:\\Users\\amitabha.roy\\AppData\\Local\\Programs\\Python\\Python312\\python.exe";

    public ExecutionResult executePython(CodeDTO code, Path folderPath, String input) throws IOException, InterruptedException {
        // Write the Python code and custom input to temporary files
        String script = code.getSubmittedCode();
        String inputFileName = "input.txt";
        String scriptFileName = "Program" + code.getCandidateId() + "." + code.getQuestionId() + ".py";
        Path inputFilePath = folderPath.resolve(inputFileName);
        Path scriptFilePath = folderPath.resolve(scriptFileName);


        // Ensure that the target directory exists
        Files.createDirectories(folderPath);

        try (BufferedWriter scriptWriter = new BufferedWriter(new FileWriter(scriptFilePath.toString()));
             BufferedWriter inputWriter = new BufferedWriter(new FileWriter(inputFilePath.toString()))) {
            scriptWriter.write(script);
            String[] customInput = input.split("\\s+");
            for (String element : customInput) {
                inputWriter.write(element + System.lineSeparator());
            }
        } catch (IOException e) {
            logger.error("An error occurred: ", e);
            return new ExecutionResult(false, "Error writing code or input to file: " + e.getMessage(), null);
        }

        // Construct the Python command
        ProcessBuilder processBuilder = new ProcessBuilder(pythonExecutable, scriptFilePath.toString());
        processBuilder.directory(folderPath.toFile());

        // Redirect standard input to read from the input file
        processBuilder.redirectInput(inputFilePath.toFile());

        // Start the process
        Process runProcess = processBuilder.start();

        // Close the resources
        try {
            runProcess.waitFor();  // Ensure the process has completed before destroying
        } catch (InterruptedException e) {
            logger.error("Error waiting for process completion", e);
        }
        runProcess.destroy();

        // Capture the output and error streams
        InputStream inputStream = runProcess.getInputStream();
        InputStream errorStream = runProcess.getErrorStream();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream))) {

            StringBuilder output = new StringBuilder();
            StringBuilder errors = new StringBuilder();

            // Read the output of the Python script
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Read the error stream of the Python script
            while ((line = errorReader.readLine()) != null) {
                errors.append(line).append("\n");
            }

            if (!output.isEmpty()) {
                output.replace(output.length() - 1, output.length(), "");
            }

            if (runProcess.exitValue() == 0) {
                return new ExecutionResult(true, output.toString(), errors.toString());
            } else {
                return new ExecutionResult(false, output.toString(), errors.toString());
            }
        }
    }
}
