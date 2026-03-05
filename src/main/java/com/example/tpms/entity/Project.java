package com.example.tpms.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Project{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public String title;

    @ElementCollection
    @CollectionTable(name = "project_technologies", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "technology", columnDefinition = "TEXT")
    public List<String> technologies;

    @ElementCollection
    @CollectionTable(name = "project_details", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "detail", columnDefinition = "TEXT")
    public List<String> details;
}
