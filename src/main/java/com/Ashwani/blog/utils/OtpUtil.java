package com.Ashwani.blog.utils;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;

@Component
public class OtpUtil {
    private static final SecureRandom random = new SecureRandom();

    public String generateOtp() {
        int number = random.nextInt(1_000_000); // 0 to 999999 inclusive
        return String.format("%06d", number);   // always 6 digits with leading zeros
    }
}

