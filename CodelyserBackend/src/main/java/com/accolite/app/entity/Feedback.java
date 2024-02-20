package com.accolite.app.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Feedback {
    @Id
    private Long id;
    @Column(columnDefinition = "LONGTEXT")
    private String description;
    private Integer rating;
}
