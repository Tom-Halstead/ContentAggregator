package com.contentaggregator.service;

import com.contentaggregator.model.User;
import com.contentaggregator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserInfoByCognitoUuid(String cognitoUuid) {
        return userRepository.findByCognitoUuid(cognitoUuid).orElseThrow(()-> new RuntimeException("User not found"));
    }

    public User saveOrUpdateUser(String username, String email, String cognitoUuid) {
        Optional<User> existingUser = userRepository.findByCognitoUuid(cognitoUuid);

        if (existingUser.isPresent()) {
            // ✅ User exists, update lastLogin
            User user = existingUser.get();
            return userRepository.save(updateLastLogin(user));
        } else {
            // ✅ New user, save to DB
            User newUser = new User(username, email, cognitoUuid);
            return userRepository.save(newUser);
        }
    }

    public User updateLastLogin(User user) {
        user.setLastLogin(LocalDateTime.now());
        return userRepository.save(user);
    }


    public Optional<User> getUserByCognitoUuid(String cognitoUuid) {
        return userRepository.findByCognitoUuid(cognitoUuid);
    }

}
