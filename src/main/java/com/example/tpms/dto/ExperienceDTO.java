package com.example.tpms.dto;

import lombok.Data;

import java.util.List;

@Data
public class ExperienceDTO {
    private String company;
    private String role;
    private String location;
    private String duration;
    private List<String> highlights;
}
