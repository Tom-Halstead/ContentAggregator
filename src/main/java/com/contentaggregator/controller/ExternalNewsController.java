package com.contentaggregator.controller;

import com.contentaggregator.model.NewsSource;
import com.contentaggregator.service.ExternalNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class ExternalNewsController {


    private final ExternalNewsService externalNewsService;

    @Autowired
    public ExternalNewsController(ExternalNewsService externalNewsService) {
        this.externalNewsService = externalNewsService;
    }


    // GET /api/news/sources/{apiType}/articles?category=technology
    @GetMapping("/sources/{apiType}/articles")
    public ResponseEntity<List<NewsSource>> getArticlesBySource(
            @PathVariable String apiType,
            @RequestParam String category) {
        List<NewsSource> articles = externalNewsService.fetchArticlesBySource(apiType, category);
        return ResponseEntity.ok(articles);
    }

    // GET /api/news/categories/{category}/articles
    @GetMapping("/categories/{category}/articles")
    public ResponseEntity<List<NewsSource>> getArticlesByCategory(@PathVariable String category) {
        List<NewsSource> articles = externalNewsService.fetchArticlesByCategory(category);
        return ResponseEntity.ok(articles);
    }

    // GET /api/news/users/{userId}/articles
    @GetMapping("/users/{userId}/articles")
    public ResponseEntity<List<NewsSource>> getArticlesByUser(@PathVariable int userId) {
        List<NewsSource> articles = externalNewsService.fetchArticlesByUserPreferences(userId);
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/articles")
    public ResponseEntity<List<NewsSource>> getArticles(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String query,
            @RequestParam(required = false, defaultValue = "en") String language,
            @RequestParam(required = false, defaultValue = "publishedAt") String sortBy,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "1") int page
    ) {
        List<NewsSource> articles = externalNewsService.fetchArticles(source, category, query, language, sortBy, pageSize, page);
        return ResponseEntity.ok(articles);
    }


}
