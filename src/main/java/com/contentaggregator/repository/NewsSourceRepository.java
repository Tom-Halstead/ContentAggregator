package com.contentaggregator.repository;

import com.contentaggregator.model.NewsSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsSourceRepository extends JpaRepository<NewsSource, Integer> {
    Optional<NewsSource> findByName(String name);

    List<NewsSource> findByCategory(String category);
}