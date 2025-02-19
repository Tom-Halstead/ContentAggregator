package com.contentaggregator.service;

import com.contentaggregator.model.NewsSource;
import com.contentaggregator.repository.NewsSourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NewsSourceService {
    private final NewsSourceRepository newsSourceRepository;

    public NewsSourceService(NewsSourceRepository newsSourceRepository) {
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