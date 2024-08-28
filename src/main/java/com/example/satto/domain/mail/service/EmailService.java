package com.example.satto.domain.mail.service;

import com.example.satto.domain.users.entity.Users;

public interface EmailService {
    String sendSimpleMessage(String to)throws Exception;

    String sendFindPwMessage(String toEmail) throws Exception;
}
