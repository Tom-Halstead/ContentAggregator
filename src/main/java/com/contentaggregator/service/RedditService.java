package com.contentaggregator.service;


import com.contentaggregator.dto.RedditPostDTO;
import com.contentaggregator.exception.RedditApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
@Service
public class RedditService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public RedditService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }


    /**
     * Fetches top posts from a specific subreddit.
     *
     * @param limit    Number of posts to retrieve.
     * @param category Subreddit name.
     * @return List of RedditPostDTO containing relevant post data.
     */
    public List<RedditPostDTO> fetchRedditListings(int limit, String category) {
        // Define possible time filters
        String[] timeFilters = {"hour", "day", "week", "month", "year", "all"};
        // Pick one at random
        String randomTime = timeFilters[new Random().nextInt(timeFilters.length)];
        String encodedCategory = URLEncoder.encode(category, StandardCharsets.UTF_8);
        String apiUrl = String.format("https://www.reddit.com/r/%s/top.json?t=%s&limit=%d",
                encodedCategory, randomTime, limit);
        return fetchPostsFromApi(URI.create(apiUrl));
    }


    /**
     * Makes the API call to Reddit and maps the response to RedditPostDTO list.
     *
     * @param uri Fully constructed Reddit API URI.
     * @return List of RedditPostDTO.
     */
    public List<RedditPostDTO> fetchPostsFromApi(URI uri) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.USER_AGENT, "ContentAggregatorApp/1.0");

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    uri, HttpMethod.GET, requestEntity, JsonNode.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode children = response.getBody().path("data").path("children");

                if (children.isArray()) {
                    List<RedditPostDTO> posts = new ArrayList<>();
                    for (JsonNode childNode : children) {
                        JsonNode postData = childNode.path("data");
                        RedditPostDTO post = objectMapper.treeToValue(postData, RedditPostDTO.class);
                        posts.add(post);
                    }
                    return posts;
                }
            }

            throw new RedditApiException("Failed to fetch Reddit posts. Status: " + response.getStatusCode());

        } catch (HttpClientErrorException e) {
            // Handle specific HTTP exceptions from the Reddit API
            throw new RedditApiException("Reddit API request failed: " + e.getStatusCode() + " - ");
        } catch (Exception e) {
            // Catch any unexpected exceptions and wrap them in a custom exception
            throw new RedditApiException("An error occurred while fetching Reddit posts", e);
        }
    }

    /**
     * Fetch posts based on user's preferred subreddits (assumed stored in the database).
     *
     * @param userId The user ID.
     * @return List of RedditPostDTO.
     */
    public List<RedditPostDTO> fetchPostsByUserPreferences(int userId) {
        List<String> preferredSubreddits = List.of("technology", "news");

        List<RedditPostDTO> posts = new ArrayList<>();
        for (String subreddit : preferredSubreddits) {
            posts.addAll(fetchRedditListings(5, subreddit));
        }
        return posts;
    }


}
