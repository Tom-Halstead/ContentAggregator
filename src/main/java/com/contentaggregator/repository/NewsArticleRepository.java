package com.contentaggregator.repository;

import com.contentaggregator.model.NewsArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, Integer> {
    Optional<NewsArticle> findByName(String name);

    List<NewsArticle> findByCategory(String category);
}