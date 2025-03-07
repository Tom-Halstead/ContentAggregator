package com.contentaggregator.repository;

import com.contentaggregator.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByCognitoUuid(String cognitoUuid);
    Optional<User> findByEmail(String email);
}
