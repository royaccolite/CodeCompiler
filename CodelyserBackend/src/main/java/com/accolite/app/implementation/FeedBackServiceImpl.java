package com.accolite.app.implementation;

import com.accolite.app.dto.FeedbackDTO;
import com.accolite.app.entity.Feedback;
import com.accolite.app.repository.FeedBackServiceRepository;
import com.accolite.app.service.FeedBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedBackServiceImpl implements FeedBackService {

    @Autowired
    FeedBackServiceRepository feedBackServiceRepository;
    @Override
    public void receiveUserFeedback(FeedbackDTO feedbackDTO) {
        feedBackServiceRepository.save(new Feedback(feedbackDTO.getCandidateId(),feedbackDTO.getDescription(),feedbackDTO.getRating()));
    }

    @Override
    public Feedback getUserFeedback(Long id) {
        return feedBackServiceRepository.findById(id).get();
    }

}
