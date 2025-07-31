package com.AAZl3l4.UserServe.utils;

import com.AAZl3l4.common.pojo.AopLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MailService {

    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String mail;

    // 发送简单文本邮件
    @AopLog("发送邮件")
    @Async("asyncExecutor")
    public void sendText(String to, String subject, String content) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(mail);
        msg.setTo(to); //发给谁
        msg.setSubject(subject); //主题
        msg.setText(content); //内容
        mailSender.send(msg);
    }
}