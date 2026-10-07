package com.caching.spring_caching_abstraction.service;

import com.caching.spring_caching_abstraction.dto.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserProfileService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void saveUserProfile(UserProfile user, Long userId){
        String key = "userProfile:"+userId;

        Map<String, String> userData = new HashMap<>();
        userData.put("name", user.name());
        userData.put("age", user.age());
        userData.put("gender", user.gender());

        stringRedisTemplate.opsForHash().putAll(key, userData);
    }

    public UserProfile getFullProfile(Long userId) {
        String key = "userProfile:"+userId;
        Map<Object, Object> response = stringRedisTemplate.opsForHash().entries(key);

        return new UserProfile(userId, (String) response.get("name"), (String) response.get("age"), (String) response.get("gender") );
    }
}