package com.contentaggregator.controller;


import com.contentaggregator.dto.RedditPostDTO;
import com.contentaggregator.service.RedditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reddit")
public class RedditController {

    private final RedditService redditService;

    @Autowired
    public RedditController(RedditService redditService) {
        this.redditService = redditService;
    }

    /**
     * ✅ GET /api/reddit/subreddits/{subreddit}/posts?limit=10
     * Fetch top posts from a specific subreddit with an optional limit.
     */
    @GetMapping("/subreddits/{subreddit}/posts")
    public ResponseEntity<List<RedditPostDTO>> getPostsBySubreddit(
            @PathVariable String subreddit,
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<RedditPostDTO> posts = redditService.fetchRedditListings(limit, subreddit);
        return ResponseEntity.ok(posts);
    }

    /**
     * ✅ GET /api/reddit/categories/{category}/posts?limit=10
     * Fetch top posts based on a category (mapped to subreddit).
     */
    @GetMapping("/categories/{category}/posts")
    public ResponseEntity<List<RedditPostDTO>> getPostsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<RedditPostDTO> posts = redditService.fetchRedditListings(limit, category);
        return ResponseEntity.ok(posts);
    }

    /**
     * ✅ GET /api/reddit/users/{userId}/posts
     * Fetch posts based on user's preferred subreddits (if stored in the database)
     */
    @GetMapping("/users/{userId}/posts")
    public ResponseEntity<List<RedditPostDTO>> getPostsByUserPreferences(@PathVariable int userId) {
        List<RedditPostDTO> posts = redditService.fetchPostsByUserPreferences(userId);
        return ResponseEntity.ok(posts);
    }

    /**
     * ✅ GET /api/reddit/posts?subreddit=technology&limit=10
     * Flexible endpoint to fetch posts with optional subreddit and limit parameters.
     */
    @GetMapping("/posts")
    public ResponseEntity<List<RedditPostDTO>> getPosts(
            @RequestParam(required = false, defaultValue = "all") String subreddit,
            @RequestParam(required = false, defaultValue = "5") int limit
    ) {
        List<RedditPostDTO> posts = redditService.fetchRedditListings(limit, subreddit);
        return ResponseEntity.ok(posts);
    }
}
