package com.caching.spring_caching_abstraction.dto;

public record UserProfile(
        Long userId,
        String name,
        String age,
        String gender
) {
}
