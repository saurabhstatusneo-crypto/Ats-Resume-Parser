package com.example.tpms.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public String company;
    public String role;
    public String location;
    public String duration;

    @ElementCollection // Ye highlights ke liye alag table bana dega
    @Column(columnDefinition = "TEXT") // Har highlight ki length ki tension khatam
    @CollectionTable(name = "experience_highlights", joinColumns = @JoinColumn(name = "experience_id"))
    public List<String> highlights;
}
