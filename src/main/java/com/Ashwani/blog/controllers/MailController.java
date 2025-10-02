package com.Ashwani.blog.controllers;

import com.Ashwani.blog.domain.dtos.EmailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/api/v1/mail")
@RequiredArgsConstructor
public class MailController {

    private final JavaMailSender javaMailSender;
    @PostMapping("/send-email")
    public String sendEmail(@RequestBody EmailDto emailDto){

        SimpleMailMessage simpleMailMessage= new SimpleMailMessage();
        simpleMailMessage.setTo(emailDto.getTo());
        simpleMailMessage.setSubject(emailDto.getSubject());
        simpleMailMessage.setText(emailDto.getText());
        javaMailSender.send(simpleMailMessage);
        return "Email sent  successfully";
    }
}
