package com.accolite.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import jakarta.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TestCaseOutput {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(columnDefinition = "LONGTEXT")
    private String actualOutput;
    @Column(columnDefinition = "LONGTEXT")
    private String expectedOutput;

    private Boolean exitValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_submission_id", nullable = false)
    @JsonIgnore
    private TestSubmission testSubmission;
    public TestCaseOutput(String actualOutput, String expectedOutput, Boolean exitValue) {
        this.actualOutput = actualOutput;
        this.expectedOutput = expectedOutput;
        this.exitValue = exitValue;
    }
}
