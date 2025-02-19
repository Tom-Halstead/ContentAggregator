package com.contentaggregator.controller;


import com.contentaggregator.model.NewsArticle;
import com.contentaggregator.service.NewsArticleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/news-sources")
public class NewsArticleController {
    private final NewsArticleService newsArticleService;

    public NewsArticleController(NewsArticleService newsArticleService) {
        this.newsArticleService = newsArticleService;
    }

    @GetMapping
    public List<NewsArticle> getAllNewsSources() {
        return newsArticleService.getAllSources();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsArticle> getNewsSourceById(@PathVariable int id) {
        return newsArticleService.getSourceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
