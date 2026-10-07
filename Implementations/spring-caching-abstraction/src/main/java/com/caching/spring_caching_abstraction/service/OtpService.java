package com.caching.spring_caching_abstraction.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class OtpService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final Logger logger = Logger.getLogger(OtpService.class.getName());

    public String storeOTP(Long userId){
        String otpkey = "otp:"+userId;
        String attemptsKey = "attempt:"+userId;

        long ttl = 10;
        String otp = new SecureRandom().ints(6, 0, 10)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());

        stringRedisTemplate.opsForValue().set(otpkey, otp, Duration.ofMinutes(ttl));
        stringRedisTemplate.opsForValue().set(attemptsKey, "0", Duration.ofMinutes(ttl));

        return "OTP:"+otp;
    }

    public boolean verifyOTP(Long userId, String otp){
        String otpkey = "otp:"+userId;
        String attemptsKey = "attempt:"+userId;

        String storedOtp = stringRedisTemplate.opsForValue().get(otpkey);

        // check whether otp is expired
        if(storedOtp == null){
            logger.info("OTP is expired");
            return false;
        }

        // check whether otp is right or not
        if(storedOtp.equals(otp)){
            logger.info("OTP validated succesfully");
            stringRedisTemplate.delete(otpkey);
            stringRedisTemplate.delete(attemptsKey);
            return true;
        }

        // Increment the attempt count and accordingly take action
        long currentAttempt = stringRedisTemplate.opsForValue().increment(attemptsKey);
        logger.info("user provided invalid OTP "+currentAttempt+" times");

        if(currentAttempt == 3){
            logger.info("user is out Attempts. Deleting the keys - ask user for requesting new otp");
            stringRedisTemplate.delete(otpkey);
            stringRedisTemplate.delete(attemptsKey);
        }
        return false;

    }


}
