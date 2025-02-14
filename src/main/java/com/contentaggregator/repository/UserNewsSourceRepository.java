package com.contentaggregator.repository;

import com.contentaggregator.model.NewsSource;
import com.contentaggregator.model.User;
import com.contentaggregator.model.UserNewsSource;
import com.contentaggregator.model.UserNewsSourceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNewsSourceRepository extends JpaRepository<UserNewsSource, UserNewsSourceId> {
    List<UserNewsSource> findByUser(User user);
    List<UserNewsSource> findByNewsSource(NewsSource newsSource);
}
