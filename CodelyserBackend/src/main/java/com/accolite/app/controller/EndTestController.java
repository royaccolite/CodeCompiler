package com.accolite.app.controller;

import com.accolite.app.exception.CandidateNotFoundException;
import com.accolite.app.exception.TestResutNotFoundException;
import com.accolite.app.implementation.CodeServiceImpl;
import com.accolite.app.service.TestResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Paths;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "http://localhost:4200")
public class EndTestController {
    private final String BASE_DIR = Paths.get(System.getProperty("user.dir"))+"\\";
    @Autowired
    TestResultService testResultService;

    // will return void or string
    @PostMapping("/endTest")
    public ResponseEntity<?> endTest(@RequestParam Long candidateId) {
        try {
            CodeServiceImpl.deleteDirectory(Paths.get(BASE_DIR + "Candidate" + candidateId));
            testResultService.endTest(candidateId);
            return ResponseEntity.ok().build();
        } catch (CandidateNotFoundException | NullPointerException | TestResutNotFoundException e) {
            // Handle specific exception
            String errorMessage = "Error with ending test for ID: " + candidateId;
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        } catch (Exception e) {
            // Handle generic exception
            String errorMessage = "Internal Server Error";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }
}