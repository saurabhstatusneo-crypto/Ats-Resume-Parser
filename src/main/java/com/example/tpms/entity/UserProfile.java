package com.example.tpms.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "user_profile")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String name;
    private String title;
    private String email;
    private String phone;
    private String location;
    private String linkedin;
    private String github;
    private String role;
    private String password;

    @Column(columnDefinition = "TEXT")
    private String professionalSummary;

    @OneToMany(cascade = CascadeType.ALL)
    private List<userEducation> education;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Experience> experience;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Project> projects;

    @OneToOne(cascade = CascadeType.ALL)
    private Skills skills;
}




