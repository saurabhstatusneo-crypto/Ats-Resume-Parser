package com.example.tpms.controller;

import com.example.tpms.dto.UserProfileDto;
import com.example.tpms.service.CandidateService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/candidate")
@AllArgsConstructor
public class CandidateController {

     private final CandidateService candidateService;

    @GetMapping("/profile/{id}")
    public ResponseEntity<UserProfileDto> getCandidateProfile(@PathVariable Long id) {
        try {
            UserProfileDto profile = candidateService.getCandidateProfileComplete(id);
            if (profile != null) {
                return ResponseEntity.ok(profile);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
