package com.caching.spring_caching_abstraction.controller;

import com.caching.spring_caching_abstraction.dto.UserProfile;
import com.caching.spring_caching_abstraction.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @PostMapping("/saveUserProfile/{userId}")
    public String savedUserProfile(@RequestBody UserProfile userProfile, @PathVariable Long userId){
        userProfileService.saveUserProfile(userProfile, userId);
        return "user profile saved";
    }

    @GetMapping("/getFullProfile/{userId}")
    public ResponseEntity<UserProfile> getFullProfile(@PathVariable Long userId){
        UserProfile response = userProfileService.getFullProfile(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
