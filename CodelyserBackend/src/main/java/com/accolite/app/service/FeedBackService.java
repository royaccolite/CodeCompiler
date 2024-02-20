package com.accolite.app.service;

import com.accolite.app.dto.FeedbackDTO;
import com.accolite.app.entity.Feedback;

public interface FeedBackService {
    public void receiveUserFeedback(FeedbackDTO feedbackDTO);
    public Feedback getUserFeedback(Long id);
}
