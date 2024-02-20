package com.accolite.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import jakarta.persistence.*;
import java.util.ArrayList;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TestSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private Long candidateId;
    private Long questionId;
    @Column(columnDefinition = "LONGTEXT")
    private String code;

    private boolean submitStatus;//0 or 1 0-> run and compilation failure 1->submit compilation success
    @OneToMany(mappedBy = "testSubmission", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TestCaseOutput> testCaseOutputs = new ArrayList<>();

    public TestSubmission(Long candidateId, Long questionId, String code, boolean submitStatus) {
        this.candidateId = candidateId;
        this.questionId = questionId;
        this.code = code;
        this.submitStatus = submitStatus;
    }
}
