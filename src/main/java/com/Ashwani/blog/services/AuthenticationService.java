    package com.Ashwani.blog.services;

    import com.Ashwani.blog.domain.dtos.SignUpRequest;
    import org.springframework.security.core.userdetails.UserDetails;

    public interface AuthenticationService {

        UserDetails authenticate(String email, String password);

        String generateToken(UserDetails userDetails);

        UserDetails validateToken(String token);

        UserDetails signUp(SignUpRequest request);

    }
