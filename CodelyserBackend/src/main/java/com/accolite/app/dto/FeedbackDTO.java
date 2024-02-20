package com.accolite.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class FeedbackDTO {
    private Long candidateId;
    private String description;
    private Integer rating;
}
