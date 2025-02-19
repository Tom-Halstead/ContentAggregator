package com.contentaggregator.controller;


import com.contentaggregator.model.NewsSource;
import com.contentaggregator.service.NewsSourceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/news-sources")
public class NewsSourceController {
    private final NewsSourceService newsSourceService;

    public NewsSourceController(NewsSourceService newsSourceService) {
        this.newsSourceService = newsSourceService;
    }

    @GetMapping
    public List<NewsSource> getAllNewsSources() {
        return newsSourceService.getAllSources();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsSource> getNewsSourceById(@PathVariable int id) {
        return newsSourceService.getSourceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
