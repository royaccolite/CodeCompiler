package com.accolite.app.controller;

import com.accolite.app.exception.FeedbackNotFoundException;
import com.accolite.app.dto.FeedbackDTO;
import com.accolite.app.entity.Feedback;
import com.accolite.app.exception.InvalidFeedbackException;
import com.accolite.app.service.FeedBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "http://localhost:4200")
public class FeedBackController {

    @Autowired
    FeedBackService feedBackService;

    // will return list<Feedback> or String
    @GetMapping("/{id}")
    public ResponseEntity<?> getFeedback(@PathVariable Long id) {
        try {
            Feedback feedback = feedBackService.getUserFeedback(id);
            return ResponseEntity.ok(feedback);
        } catch (FeedbackNotFoundException | NullPointerException e) {
            // Handle specific exception
            String errorMessage = "Feedback not found for ID: " + id;
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        } catch (Exception e) {
            // Handle generic exception
            String errorMessage = "Internal Server Error";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addFeedback(@RequestBody FeedbackDTO feedbackDTO) {
        try {
            feedBackService.receiveUserFeedback(feedbackDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (InvalidFeedbackException e) {
            // Handle specific exception
            String errorMessage = "Invalid feedback data. Please check your input.";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        } catch (Exception e) {
            // Handle generic exception
            String errorMessage = "Internal Server Error";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }
}