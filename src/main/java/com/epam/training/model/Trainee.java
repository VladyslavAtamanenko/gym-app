package com.epam.training.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "trainees")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Trainee{
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private LocalDate dateOfBirth;
    private String address;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(name="trainee_trainer",
            joinColumns=@JoinColumn(name="trainee_id"),
            inverseJoinColumns=@JoinColumn(name="trainer_id"))
    List<Trainer> trainers;

    @OneToMany(
            mappedBy = "trainee",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    private List<Training> trainings;
}
