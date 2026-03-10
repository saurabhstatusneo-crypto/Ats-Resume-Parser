package com.example.tpms.dto;

import lombok.Data;

import java.util.List;

@Data
public class SkillsDTO {
    private List<String> frontEnd;
    private List<String> backEnd;
    private List<String> databases;
    private List<String> devops;
    private List<String> other;
}
