package com.Ashwani.blog.services.impl;

import com.Ashwani.blog.domain.dtos.EmailDto;
import com.Ashwani.blog.domain.dtos.SignUpRequest;
import com.Ashwani.blog.domain.entities.User;
import com.Ashwani.blog.repositories.UserRepository;
import com.Ashwani.blog.services.AuthenticationService;
import com.Ashwani.blog.utils.EmailUtil;
import com.Ashwani.blog.utils.OtpUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpUtil otpUtil;
    private final EmailUtil emailUtil;
    @Value("${jwt.secret}")
    private String secretKey;

    private final Long jwtExpiryMs = 86400000L;

    @Override
    public UserDetails authenticate(String email, String password) {
        Authentication authenticate = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));
        // load user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // check if account is active
        if (!user.isActive()) {
            throw new IllegalStateException("Your account is not verified. Please verify with OTP first.");
        }
        return userDetailsService.loadUserByUsername(email);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiryMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public UserDetails validateToken(String token) {
        String username = extractUsername(token);
        return userDetailsService.loadUserByUsername(username);
    }

    @Override
    public UserDetails signUp(SignUpRequest request) {
        // 🔒 Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already registered");
        }
        String otp = otpUtil.generateOtp();
        try {
            emailUtil.sendOtpEmail(request.getEmail(), otp);
        } catch (MessagingException ex) {
            throw new RuntimeException("Unable to send otp please try again");
        }


        // 🔑 Create new user (password encoded)
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .otp(otp)
                .active(false)
                .otpGeneratedTime(LocalDateTime.now())
                .build();

        // Save user in DB
        User savedUser = userRepository.save(user);

        // ✅ Load UserDetails for Spring Security
        return userDetailsService.loadUserByUsername(savedUser.getEmail());
    }

    @Override
    @Transactional
    public String verifyAccount(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email" + email));
        if (user.getOtp().equals(otp)
                &&
                Duration.between(user.getOtpGeneratedTime(), LocalDateTime.now()).getSeconds() < (300)) {
            user.setActive(true);
            userRepository.save(user);
            return "OTP verified you can login";
        }
        return "please regenerate otp and try again";
    }

    @Override
    public String regenerateOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));
        String otp = otpUtil.generateOtp();
        try {
            emailUtil.sendOtpEmail(email, otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send otp please try again");
        }
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);
        return "Email sent... please verify account within 5 minute";
    }

    @Override
    public String forgotPassword(String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with this email" + email));

        try {
            emailUtil.sendSetPasswordEmail(email);
        } catch (MessagingException ex) {
            throw new RuntimeException("Unable to send set password email please try again");
        }
        return "please check your email new password to your account";
    }

    @Override
    public String setPassword(String email, String newPassword) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with this email" + email));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return "New Password set successfully login with new password";
    }


    private String extractUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
