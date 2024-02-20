package com.accolite.app.implementation;

import com.accolite.app.converter.ConverterService;
import com.accolite.app.dto.AdminQuestionDTO;
import com.accolite.app.dto.CandidateDTO;
import com.accolite.app.entity.Candidate;
import com.accolite.app.entity.Question;
import com.accolite.app.entity.TestResult;
import com.accolite.app.exception.ApiRequestException;
import com.accolite.app.repository.CandidateRepository;
import com.accolite.app.repository.QuestionRepository;
import com.accolite.app.repository.TestResultRepository;
import com.accolite.app.service.AdminService;
import com.accolite.app.util.UtilityService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final ConverterService converterService;
    private final UtilityService utilityService;
    @Autowired
    CandidateRepository candidateRepository;

    @Autowired
    TestResultRepository testResultRepository;
    @Autowired
    QuestionRepository questionRepository;

    @Override
    public String saveQuestion(AdminQuestionDTO questionDTO) {
        try {
            Question question = questionRepository.save(converterService.convertQuestionToEntity(questionDTO));
            if (questionDTO.getType().equals("Database")) {
                question.setCommands(questionDTO.getCommands());
                question.setQuery(questionDTO.getQuery());
            } else {
                question.setTemplates(converterService.convertTemplatesToEntity(questionDTO.getTemplates(), question));
                question.setTestCases(converterService.convertTestcasesToEntity(questionDTO.getTestcases(), question));
                question.setCompilationTimeout(questionDTO.getCompilationTimeout());
            }
            questionRepository.save(question);
            return "Question Saved";
        } catch (DataIntegrityViolationException e) {
            throw new ApiRequestException("Duplicate Entry", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public List<AdminQuestionDTO> getQuestions() {
        List<Question> questions = questionRepository.findAll();
        List<AdminQuestionDTO> list = new ArrayList<>();
        questions.forEach(
                (x) -> {
                    list.add(converterService.convertQuestionToDTO(x));
                }
        );
        return list;
    }

    @Override
    public List<CandidateDTO> uploadData(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ApiRequestException("File is Empty", HttpStatus.BAD_REQUEST);
        }
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            List<CandidateDTO> list = new ArrayList<>();
            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getLastRowNum();
            for (int i = 0; i <=rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row!=null) {
                    CandidateDTO candidateDTO = new CandidateDTO();
                    candidateDTO.setEmail(row.getCell(0).getStringCellValue());
                    candidateDTO.setPassword(utilityService.hashPassword(candidateDTO.getEmail()));
                    list.add(candidateDTO);
                }
            }

            return list;
        } catch (IOException e) {
            throw new ApiRequestException("File Not Found", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public String assignQuestion(List<CandidateDTO> candidates) {
        try {
            List<Question> questions = new ArrayList<>();
            candidates.get(0).getQuestions().forEach(
                    x -> questions.add(questionRepository.findById(x.getId()).get())
            );
            candidates.forEach(
                    candidateDTO -> {
                            candidateRepository.save(converterService.convertCandidateToEntity(candidateDTO, questions));

                    }
            );
            return "Questions Assigned";
        } catch (DataIntegrityViolationException e) {
            throw new ApiRequestException("Duplicate Entry", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public List<CandidateDTO> getCandidates() {
        List<Candidate> candidates = candidateRepository.findAll();
        List<CandidateDTO> list = new ArrayList<>();
        candidates.forEach(
                (x) -> {
                    TestResult result = testResultRepository.findById(x.getId()).orElse(null);
                    list.add(converterService.convertCandidateToDTO(x, result));
                }
        );
        return list;
    }
}
