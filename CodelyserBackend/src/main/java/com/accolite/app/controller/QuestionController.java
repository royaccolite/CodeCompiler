package com.accolite.app.controller;

import com.accolite.app.dto.QuestionDTO;
import com.accolite.app.exception.CandidateNotFoundException;
import com.accolite.app.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "http://localhost:4200")
public class QuestionController {
    @Autowired
    QuestionService questionService;


    @GetMapping("/questions")
    public ResponseEntity<?> getAllQuestionsByCandidateID(@RequestParam Long candidateId) {
        try {
            List<QuestionDTO> questions = questionService.getQuestionsByCandidateId(candidateId);
            return ResponseEntity.ok(questions);
        } catch (CandidateNotFoundException | NullPointerException e) {
            // Handle specific exception
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Candidate not found for ID: " + candidateId);
        } catch (Exception e) {
            // Handle generic exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }
}
