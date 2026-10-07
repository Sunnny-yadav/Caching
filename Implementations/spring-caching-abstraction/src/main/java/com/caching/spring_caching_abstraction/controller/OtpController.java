package com.caching.spring_caching_abstraction.controller;

import com.caching.spring_caching_abstraction.dto.OtpVerifyRequest;
import com.caching.spring_caching_abstraction.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class OtpController {

    @Autowired
    private OtpService otpService;

    @GetMapping("/generateOtp/{userId}")
    public String generateOtp(@PathVariable("userId") Long userId){
        return otpService.storeOTP(userId);
    }

    @PostMapping("/verifyOtp/{userId}")
    public boolean verifyOtp(@PathVariable("userId") Long userId, @RequestBody OtpVerifyRequest request){
        return otpService.verifyOTP(userId, request.otp());
    }

}
