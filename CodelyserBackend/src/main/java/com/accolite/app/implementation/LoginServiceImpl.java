package com.accolite.app.implementation;

import com.accolite.app.entity.Candidate;
import com.accolite.app.repository.CandidateRepository;
import com.accolite.app.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    CandidateRepository candidateRepository;
    @Override
    public Long validateCandidate(String email) {
        Optional<Candidate> candidateOptional = Optional.ofNullable(candidateRepository.findByEmail(email));
        return candidateOptional.map(Candidate::getId).orElse(null);
    }
}
