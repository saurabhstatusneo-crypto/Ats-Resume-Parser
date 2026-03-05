package com.example.tpms.service;

import com.example.tpms.entity.UserProfile;
import com.example.tpms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserProfile saveUserFromJson(String jsonString) throws Exception {
        UserProfile userProfile = objectMapper.readValue(jsonString, UserProfile.class);
        return repository.save(userProfile);
    }
}