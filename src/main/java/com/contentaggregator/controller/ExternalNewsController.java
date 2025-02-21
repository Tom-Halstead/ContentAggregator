package com.contentaggregator.controller;

import com.contentaggregator.dto.NewsArticleDTO;
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

    /**
     * ✅ GET /api/news/sources/{apiType}/articles?category=technology
     * Fetch articles by source (apiType) and category.
     */
    @GetMapping("/sources/{apiType}/articles")
    public ResponseEntity<List<NewsArticleDTO>> getArticlesBySource(
            @PathVariable String apiType,
            @RequestParam(required = false) String category
    ) {
        List<NewsArticleDTO> articles = externalNewsService.fetchArticlesBySource(apiType, category);
        return ResponseEntity.ok(articles);
    }

    /**
     * ✅ GET /api/news/categories/{category}/articles
     * Fetch articles by category.
     */
    @GetMapping("/categories/{category}/articles")
    public ResponseEntity<List<NewsArticleDTO>> getArticlesByCategory(@PathVariable String category) {
        List<NewsArticleDTO> articles = externalNewsService.fetchArticlesByCategory(category);
        return ResponseEntity.ok(articles);
    }

    /**
     * ✅ GET /api/news/users/{userId}/articles
     * Fetch articles based on user's preferred news sources.
     */
    @GetMapping("/users/{userId}/articles")
    public ResponseEntity<List<NewsArticleDTO>> getArticlesByUser(@PathVariable int userId) {
        List<NewsArticleDTO> articles = externalNewsService.fetchArticlesByUserPreferences(userId);
        return ResponseEntity.ok(articles);
    }

    /**
     * ✅ GET /api/news/articles?source=bbc-news&category=technology&query=AI
     * Flexible endpoint to fetch articles with various filters.
     */
    @GetMapping("/articles")
    public ResponseEntity<List<NewsArticleDTO>> getArticles(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String query,
            @RequestParam(required = false, defaultValue = "en") String language,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "1") int page
    ) {
        List<NewsArticleDTO> articles = externalNewsService.fetchArticles(source, category, query, language, pageSize, page);
        return ResponseEntity.ok(articles);
    }
}


