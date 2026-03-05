package com.example.tpms.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Skills {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @Column(columnDefinition = "TEXT")
    private List<String> front_end;

    @ElementCollection
    @Column(columnDefinition = "TEXT")
    private List<String> back_end;

    @ElementCollection
    @Column(columnDefinition = "TEXT")
    private List<String> databases;

    @ElementCollection
    @Column(columnDefinition = "TEXT")
    private List<String> devops;

    @ElementCollection
    @Column(columnDefinition = "TEXT")
    private List<String> other;
}