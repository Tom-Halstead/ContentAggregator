package com.contentaggregator.service;

import com.contentaggregator.model.NewsArticle;
import com.contentaggregator.repository.NewsArticleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NewsArticleService {
    private final NewsArticleRepository newsArticleRepository;

    public NewsArticleService(NewsArticleRepository newsArticleRepository) {
        this.newsArticleRepository = newsArticleRepository;
    }

    public List<NewsArticle> getAllSources() {
        return newsArticleRepository.findAll();
    }

    public Optional<NewsArticle> getSourceById(int id) {
        return newsArticleRepository.findById(id);
    }

    public NewsArticle addNewsSource(NewsArticle newsArticle) {
        return newsArticleRepository.save(newsArticle);
    }
}