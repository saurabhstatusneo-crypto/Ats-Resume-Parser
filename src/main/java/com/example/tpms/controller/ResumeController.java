package com.example.tpms.controller;

import com.example.tpms.entity.UserProfile;
import com.example.tpms.service.CandidateService;
import com.example.tpms.service.GroqService;
import com.example.tpms.service.ResumeService;
import com.example.tpms.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;


@RestController
@RequestMapping("/api/resumes")
@AllArgsConstructor
public class ResumeController {

    private final CandidateService candidateService;
    private final ResumeService resumeService;
    private final ObjectMapper objectMapper;
    private final GroqService groqService;
    private final UserService userService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(@RequestParam("file") MultipartFile file) {
        try {
            String content = resumeService.parseText(file);
            String structuredData = groqService.getStructuredData(content);
            resumeService.parseTables(file);
            UserProfile userProfile = candidateService.saveUserFromJson(structuredData);
            return ResponseEntity.ok(userProfile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    @PostMapping("/new/upload")
    public ResponseEntity<Object> handleResumeUpload(@RequestParam("file") MultipartFile file) {
        try {
            Object o = groqService.processResumeDirectly(file);
            String structuredData = objectMapper.writeValueAsString(o);
            UserProfile userProfile = candidateService.saveUserFromJson(structuredData);
            return ResponseEntity.ok(userProfile);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
