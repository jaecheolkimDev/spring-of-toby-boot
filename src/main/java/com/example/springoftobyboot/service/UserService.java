package com.example.springoftobyboot.service;

import com.example.springoftobyboot.code.Level;
import com.example.springoftobyboot.entity.UserEntity;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.example.springoftobyboot.repository.UserRepository;

import java.util.List;
import java.util.Properties;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

    public UserEntity saveUser(String name, String password) {
        UserEntity user = UserEntity.createUser(name, password);
        return userRepository.save(user);
    }

    public void deleteAll() {
        userRepository.deleteAll();
    }

    public long getCount() {
        return userRepository.count();
    }

    public void upgradeLevels() {
        List<UserEntity> users = userRepository.findAll();
        for(UserEntity user : users) {
            if(canUpgradeLevel(user)) {
                upgradeLevel(user);
            }
        }
    }

    private boolean canUpgradeLevel(UserEntity user) {
        Level currentLevel = user.getLevel();
        switch (currentLevel) {
            case BASIC: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
            case SILVER: return (user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD);
            case GOLD: return false;
            default: throw new IllegalArgumentException("Unknown Level: " + currentLevel);
        }
    }

    private void upgradeLevel(UserEntity user) {
        user.upgradeLevel();
        userRepository.save(user);
    }

    private void sendUpgradeEMail(UserEntity user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("jcdoom@naver.com");
        message.setTo("받는사람 이메일");
        message.setSubject("제목");
        message.setText("내용");
        mailSender.send(message);
    }
}
