package com.contentaggregator.service;

import com.contentaggregator.dto.NewsArticleDTO;
import com.contentaggregator.exception.InvalidNewsRequestException;
import com.contentaggregator.exception.NewsApiException;
import com.contentaggregator.response.NewsApiResponse;
import lombok.Getter;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Getter
@Service
public class ExternalNewsService {

    private final RestTemplate restTemplate;

    private final String NEWS_API_KEY;

    @Autowired
    public ExternalNewsService(RestTemplate restTemplate, @Value("${news.api.key}") String NEWS_API_KEY) {
        this.restTemplate = restTemplate;
        this.NEWS_API_KEY = NEWS_API_KEY;
    }

    /**
     * Fetch articles by source and category.
     */
    public List<NewsArticleDTO> fetchArticlesBySource(String apiType, String category) {
        Map<String, String> userParams = new HashMap<>();
        userParams.put("q", category); // You can modify or add more params based on user preferences
        userParams.put("sources", apiType); // The source defined by the user

        return fetchArticlesFromApi("https://newsapi.org/v2/everything", userParams);
    }

    /**
     * Fetch articles by category (uses top-headlines endpoint).
     */
    public List<NewsArticleDTO> fetchArticlesByCategory(String category) {
        Map<String, String> userParams = new HashMap<>();
        userParams.put("category", category);

        return fetchArticlesFromApi("https://newsapi.org/v2/top-headlines", userParams);
    }

    /**
     * Fetch articles based on a user's preferred sources.
     */
    public List<NewsArticleDTO> fetchArticlesByUserPreferences(int userId) {
        // Replace with actual repository call to fetch preferred sources for the user
        List<String> userPreferredSources = List.of("techcrunch", "bbc-news");

        List<NewsArticleDTO> aggregatedArticles = new ArrayList<>();
        for (String source : userPreferredSources) {
            Map<String, String> userParams = new HashMap<>();
            userParams.put("sources", source); // User's preferred source

            // Fetch articles from each preferred source
            aggregatedArticles.addAll(fetchArticlesFromApi("https://newsapi.org/v2/everything", userParams));
        }
        return aggregatedArticles;
    }

    public List<NewsArticleDTO> fetchArticles(String category, String query, String country, int page) {
        int pageSize = 20;
        StringBuilder apiUrl;

        // If a country is provided, use the top-headlines endpoint
        if (country != null && !country.isEmpty()) {
            apiUrl = new StringBuilder("https://newsapi.org/v2/top-headlines?");
            // Include category if provided (for additional filtering)
            if (category != null && !category.isEmpty()) {
                apiUrl.append("category=")
                        .append(URLEncoder.encode(category, StandardCharsets.UTF_8))
                        .append("&");
            }
            // Append the country parameter
            apiUrl.append("country=")
                    .append(URLEncoder.encode(country, StandardCharsets.UTF_8))
                    .append("&");
        } else {
            // If no country is provided, fall back to the everything endpoint
            apiUrl = new StringBuilder("https://newsapi.org/v2/everything?");
            if (query != null && !query.isEmpty()) {
                apiUrl.append("q=")
                        .append(URLEncoder.encode(query, StandardCharsets.UTF_8))
                        .append("&");
            }
        }

        // Append pagination parameters
        apiUrl.append("pageSize=").append(pageSize)
                .append("&page=").append(page)
                .append("&apiKey=").append(getNEWS_API_KEY());

        return fetchArticlesFromApi(apiUrl.toString(), new HashMap<>());
    }


    /**
     * Handles the actual API call and maps the response to a list of NewsArticleDTO.
     */
    private List<NewsArticleDTO> fetchArticlesFromApi(String baseUrl, Map<String, String> userParams) {
        try {

            // Build the API URL dynamically using URIBuilder
            URIBuilder uriBuilder = new URIBuilder(baseUrl);

            // Add each user-defined parameter to the URI
            for (Map.Entry<String, String> param : userParams.entrySet()) {
                uriBuilder.addParameter(param.getKey(), param.getValue());
            }

            // Check if the API key is already appended to the URL
            String apiUrl = uriBuilder.toString();
            if (!apiUrl.contains("apiKey=")) {
                // Append the API key if not already present
                uriBuilder.addParameter("apiKey", getNEWS_API_KEY());
            }

            // Build the URI from the URIBuilder
            URI uri = uriBuilder.build();

            // Set up headers
            HttpHeaders headers = new HttpHeaders();

            // Set up HTTP request entity
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Use RestTemplate to send the GET request
            ResponseEntity<NewsApiResponse> response = restTemplate.exchange(uri, HttpMethod.GET, entity, NewsApiResponse.class);

            // Check if the response is successful
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<NewsArticleDTO> articles = response.getBody().getArticles();
                return articles != null ? articles : Collections.emptyList();
            } else {
                throw new NewsApiException("Failed to fetch news articles. Status: " + response.getStatusCode());
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Handle HTTP errors (e.g., 4xx or 5xx responses)
            throw new NewsApiException("API request failed with status: " + e.getStatusCode(), e);
        } catch (InvalidNewsRequestException e) {
            // Pass through the exception for global handling
            throw e;
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            throw new NewsApiException("An unexpected error occurred while fetching news articles.", e);
        }
    }


}
