package com.accolite.app.controller;

import com.accolite.app.dto.AdminQuestionDTO;
import com.accolite.app.dto.CandidateDTO;
import com.accolite.app.dto.QuestionDTO;
import com.accolite.app.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4201")

public class AdminController {
    @Autowired
    AdminService adminService;

    @PostMapping("/save")
    public ResponseEntity<Map<String, String>> saveQuestion(@RequestBody AdminQuestionDTO questionDTO) {
        String res = adminService.saveQuestion(questionDTO);
        Map<String, String> response = new HashMap<>();
        response.put("result", res);
        return ResponseEntity.ok(response);

    }
    @GetMapping("/question/all")
    public List<AdminQuestionDTO> getQuestions() {
        return adminService.getQuestions();
    }

    @PostMapping("/upload")
    public List<CandidateDTO> uploadData(@RequestParam("file") MultipartFile file) {
        return adminService.uploadData(file);
    }
    @PostMapping("/assign")
    public ResponseEntity<Map<String, String>> assignTest(@RequestBody List<CandidateDTO> candidates) {
        Map<String, String> response = new HashMap<>();
        response.put("result", adminService.assignQuestion(candidates));
        return ResponseEntity.ok(response);
    }
    @GetMapping("/candidate/all")
    public List<CandidateDTO> getCandidates() {
        return adminService.getCandidates();
    }
}
