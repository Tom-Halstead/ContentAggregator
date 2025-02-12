package com.contentaggregator.repository;

import com.contentaggregator.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByCognitoUuid(String cognitoUuid);

}
