package com.example.tpms.controller;

import com.example.tpms.entity.UserProfile;
import com.example.tpms.service.GroqService;
import com.example.tpms.service.ResumeService;
import com.example.tpms.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    Logger log = LoggerFactory.getLogger(ResumeController.class);
    @Autowired
    private ResumeService resumeService;


    @Autowired
    private GroqService groqService;

    @Autowired
    private UserService userService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(@RequestParam("file") MultipartFile file) {
        try {
            String content = resumeService.parseText(file);
            log.warn(content);
            String structuredData = groqService.getStructuredData(content);
            log.warn(structuredData);
            resumeService.parseTables(file);
            UserProfile userProfile = userService.saveUserFromJson(structuredData);
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
            // Returning the node as an Object usually triggers the correct content serialization
             return
                     ResponseEntity.ok(o);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }
}
