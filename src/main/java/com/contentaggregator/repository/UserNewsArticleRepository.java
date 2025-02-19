package com.contentaggregator.repository;

import com.contentaggregator.model.NewsArticle;
import com.contentaggregator.model.User;
import com.contentaggregator.model.UserNewsArticle;
import com.contentaggregator.model.UserNewsArticleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNewsArticleRepository extends JpaRepository<UserNewsArticle, UserNewsArticleId> {
    List<UserNewsArticle> findByUser(User user);

    List<UserNewsArticle> findByNewsSource(NewsArticle newsArticle);
}
