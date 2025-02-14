package com.contentaggregator.controller;

import com.contentaggregator.model.NewsSource;
import com.contentaggregator.service.NewsSourceService;
import com.contentaggregator.repository.NewsSourceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/news")
public class NewsController {
    private final NewsSourceRepository newsSourceRepository;

    public NewsController(NewsSourceRepository newsSourceRepository) {
        this.newsSourceRepository = newsSourceRepository;
    }


    public List<NewsSource> getAllSources() {
        return newsSourceRepository.findAll();
    }

    public Optional<NewsSource> getSourceById(int id) {
        return newsSourceRepository.findById(id);
    }

    public NewsSource addNewsSource(NewsSource newsSource) {
        return newsSourceRepository.save(newsSource);
    }
}
