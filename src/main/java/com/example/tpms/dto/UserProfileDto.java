package com.example.tpms.dto;


import lombok.Data;
import java.util.List;

@Data
public class UserProfileDto {
    private Long id;
    private String name;
    private String title;
    private String email;
    private String phone;
    private String location;
    private String linkedin;
    private String github;
    private String professionalSummary;

    // Nested Lists for related data
    private List<EducationDTO> education;
    private List<ExperienceDTO> experience;
    private List<ProjectDTO> projects;
    private SkillsDTO skills;
}
