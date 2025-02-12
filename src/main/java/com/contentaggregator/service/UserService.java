package com.contentaggregator.service;

import com.contentaggregator.model.User;
import com.contentaggregator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserInfoByCognitoUuid(String cognitoUuid) {
        return userRepository.findByCognitoUuid(cognitoUuid).orElseThrow(()-> new RuntimeException("User not found"));
    }

    public User updateLastLogin(String cognitoUuid) {
        User user = getUserInfoByCognitoUuid(cognitoUuid);
        user.setLastLogin(LocalDateTime.now());
        return userRepository.save(user);
    }
}
