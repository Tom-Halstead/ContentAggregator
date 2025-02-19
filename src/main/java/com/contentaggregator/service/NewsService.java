package com.contentaggregator.service;


import com.contentaggregator.model.NewsArticle;
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
public class NewsService {

    private final RestTemplate restTemplate;

    @Autowired
    public NewsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${news.api.key}")
    private String newsApiKey;


    public List<NewsArticle> fetchArticlesBySource(String apiType, String category) {
        String apiUrl = String.format(
                "https://newsapi.org/v2/everything?q=%s&sources=%s&apiKey=%s",
                URLEncoder.encode(category, StandardCharsets.UTF_8), apiType, newsApiKey
        );
        return fetchArticlesFromApi(apiUrl);
    }

    public List<NewsArticle> fetchArticlesByCategory(String category) {
        String apiUrl = String.format(
                "https://newsapi.org/v2/everything?q=%s&apiKey=%s",
                URLEncoder.encode(category, StandardCharsets.UTF_8), newsApiKey
        );
        return fetchArticlesFromApi(apiUrl);
    }

    public List<NewsArticle> fetchArticlesByUserPreferences(Long userId) {
        // Mock fetching user's preferred sources from the database (replace with actual repository call)
        List<String> userPreferredSources = List.of("techcrunch", "bbc-news");

        List<NewsArticle> aggregatedArticles = new ArrayList<>();
        for (String source : userPreferredSources) {
            String apiUrl = String.format(
                    "https://newsapi.org/v2/everything?sources=%s&apiKey=%s",
                    source, newsApiKey
            );
            aggregatedArticles.addAll(fetchArticlesFromApi(apiUrl));
        }
        return aggregatedArticles;
    }

    private List<NewsArticle> fetchArticlesFromApi(String apiUrl) {
        ResponseEntity<NewsApiResponse> response = restTemplate.getForEntity(apiUrl, NewsApiResponse.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody().getArticleList();
        } else {
            throw new RuntimeException("Failed to fetch news articles from external API");
        }
    }


    // NewsAPI specific
//    public List<NewsArticle> fetchNewsFromApi(NewsArticle newsArticle) {
//        // Use the external News API to fetch news articles based on the news source
//        String apiUrl = "https://newsapi.org/v2/everything?q=" + URLEncoder.encode(newsArticle.getCategory(), StandardCharsets.UTF_8)
//                + "&sources=" + newsArticle.getApiType() + "&apiKey=" + newsApiKey;
//
//        ResponseEntity<NewsApiResponse> response = restTemplate.exchange(apiUrl, HttpMethod.GET, null, NewsApiResponse.class);
//
//        if (response.getStatusCode() == HttpStatus.OK) {
//            return response.getBody().getArticleList();
//        } else {
//            throw new RuntimeException("Error fetching news from API");
//        }
//    }
}
