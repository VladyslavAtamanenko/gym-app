package com.epam.training.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "trainers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trainer{
    @Id
    private Long id;
    @ManyToOne
    private TrainingType specialization;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToMany(mappedBy = "trainers")
    List<Trainee> trainees;
}
