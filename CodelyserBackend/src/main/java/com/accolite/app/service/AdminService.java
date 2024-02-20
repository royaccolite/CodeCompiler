package com.accolite.app.service;

import com.accolite.app.dto.AdminQuestionDTO;
import com.accolite.app.dto.CandidateDTO;
import com.accolite.app.dto.QuestionDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminService {
    String saveQuestion(AdminQuestionDTO questionDTO);

    List<AdminQuestionDTO> getQuestions();
    List<CandidateDTO> uploadData(MultipartFile file);

    String assignQuestion(List<CandidateDTO> candidates);

    List<CandidateDTO> getCandidates();
}
