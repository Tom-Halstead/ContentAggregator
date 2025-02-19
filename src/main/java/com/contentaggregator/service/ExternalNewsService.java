package com.contentaggregator.service;


import com.contentaggregator.dto.NewsArticleDTO;
import com.contentaggregator.model.NewsSource;
import com.contentaggregator.response.NewsApiResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Getter
@Service
public class ExternalNewsService {

    private final RestTemplate restTemplate;
    @Value("${news.api.key}")
    private final String newsApiKey;

    @Autowired
    public ExternalNewsService(RestTemplate restTemplate, String newsApiKey) {
        this.restTemplate = restTemplate;
        this.newsApiKey = newsApiKey;
    }


    public List<NewsArticleDTO> fetchArticlesBySource(String apiType, String category) {
        String apiUrl = String.format(
                "https://newsapi.org/v2/everything?q=%s&sources=%s&apiKey=%s",
                URLEncoder.encode(category, StandardCharsets.UTF_8), apiType, newsApiKey
        );
        return fetchArticlesFromApi(apiUrl);
    }

    public List<NewsSource> fetchArticlesByCategory(String category) {
        String apiUrl = String.format(
                "https://newsapi.org/v2/everything?q=%s&apiKey=%s",
                URLEncoder.encode(category, StandardCharsets.UTF_8), newsApiKey
        );
        return fetchArticlesFromApi(apiUrl);
    }

    public List<NewsArticleDTO> fetchArticlesByUserPreferences(int userId) {
        // Mock fetching user's preferred sources from the database (replace with actual repository call)
        List<String> userPreferredSources = List.of("techcrunch", "bbc-news");

        List<NewsArticleDTO> aggregatedArticles = new ArrayList<>();
        for (String source : userPreferredSources) {
            String apiUrl = String.format(
                    "https://newsapi.org/v2/everything?sources=%s&apiKey=%s",
                    source, newsApiKey
            );
            aggregatedArticles.addAll(fetchArticlesFromApi(apiUrl));
        }
        return aggregatedArticles;
    }

    private List<NewsArticleDTO> fetchArticlesFromApi(String apiUrl) {
        ResponseEntity<NewsApiResponse> response = restTemplate.getForEntity(apiUrl, NewsApiResponse.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody().getArticles();
        } else {
            throw new RuntimeException("Failed to fetch news articles from external API");
        }
    }

    public List<NewsArticleDTO> fetchArticles(String source, String category, String query, String language, String sortBy, int pageSize, int page) {
        StringBuilder apiUrl;

        if (category != null && !category.isEmpty()) {
            apiUrl = new StringBuilder("https://newsapi.org/v2/top-headlines?");
            apiUrl.append("category=").append(category).append("&");
        } else {
            apiUrl = new StringBuilder("https://newsapi.org/v2/everything?");
            if (query != null && !query.isEmpty()) {
                apiUrl.append("q=").append(URLEncoder.encode(query, StandardCharsets.UTF_8)).append("&");
            }
            if (source != null && !source.isEmpty()) {
                apiUrl.append("sources=").append(source).append("&");
            }
            apiUrl.append("sortBy=").append(sortBy).append("&");
        }

        apiUrl.append("language=").append(language)
                .append("&pageSize=").append(pageSize)
                .append("&page=").append(page)
                .append("&apiKey=").append(newsApiKey);

        ResponseEntity<NewsApiResponse> response = restTemplate.getForEntity(apiUrl.toString(), NewsApiResponse.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody().getArticles();  // ✅ Returns List<NewsArticleDTO>
        } else {
            throw new RuntimeException("Failed to fetch news articles from external API");
        }
    }


}
