package com.accolite.app.controller;

import com.accolite.app.dto.CodeDTO;
import com.accolite.app.service.CodeService;
import com.accolite.app.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/code")
@CrossOrigin(origins = "http://localhost:4200")
public class CodeController {
    @Autowired
    CodeService codeService;

    @Autowired
    QueryService queryService;

    // will return List<String> or String
    @PostMapping("/submission")
    public ResponseEntity<?> submitCode(@RequestBody CodeDTO code) {
        if ("mysql".equals(code.getLanguage())) {
            try {
                List<Object> result = (List<Object>) queryService.submitSQlQuery(code);
                return ResponseEntity.ok(result);
            }
            catch (Exception e) {
                // Handle generic exception
                String errorMessage = "Error during code submission: " + e.getMessage();
                List<Object> result=new ArrayList<>();
                result.add(errorMessage);
                return ResponseEntity.ok(result);
                //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
            }

        }
        else{
            try {
                List<String> result = codeService.submitCode(code);
                return ResponseEntity.ok(result);

            } catch (IOException | InterruptedException | NullPointerException e) {
                String errorMessage = "Error during code submission: " + e.getMessage();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
            } catch (Exception e) {
                // Handle generic exception
                String errorMessage = "Error during code submission: " + e.getMessage();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
            }
        }

    }
}