package com.contentaggregator.controller;

import com.contentaggregator.model.NewsArticle;
import com.contentaggregator.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {


    private final NewsService newsService;

    @Autowired
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }


    // GET /api/news/sources/{apiType}/articles?category=technology
    @GetMapping("/sources/{apiType}/articles")
    public ResponseEntity<List<NewsArticle>> getArticlesBySource(
            @PathVariable String apiType,
            @RequestParam String category) {
        List<NewsArticle> articles = newsService.fetchArticlesBySource(apiType, category);
        return ResponseEntity.ok(articles);
    }

    // GET /api/news/categories/{category}/articles
    @GetMapping("/categories/{category}/articles")
    public ResponseEntity<List<NewsArticle>> getArticlesByCategory(@PathVariable String category) {
        List<NewsArticle> articles = newsService.fetchArticlesByCategory(category);
        return ResponseEntity.ok(articles);
    }

    // GET /api/news/users/{userId}/articles
    @GetMapping("/users/{userId}/articles")
    public ResponseEntity<List<NewsArticle>> getArticlesByUser(@PathVariable Long userId) {
        List<NewsArticle> articles = newsService.fetchArticlesByUserPreferences(userId);
        return ResponseEntity.ok(articles);
    }


}
