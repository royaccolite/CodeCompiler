package com.accolite.app.controller;

import com.accolite.app.exception.CandidateNotFoundException;
import com.accolite.app.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fetch")
@CrossOrigin(origins = "http://localhost:4200")
public class LoginController {
    @Autowired
    LoginService loginService;
    @GetMapping("candidateId")
    public ResponseEntity<?> validateCandidate(@RequestParam String email){
        try{
            Long candidateId = loginService.validateCandidate(email);
            return ResponseEntity.ok(candidateId);
        }
        catch (CandidateNotFoundException | NullPointerException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Candidate not found for this email :"+email);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error :");
        }
    }
}
