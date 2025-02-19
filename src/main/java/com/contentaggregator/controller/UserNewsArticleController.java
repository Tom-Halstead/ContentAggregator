package com.contentaggregator.controller;

import com.contentaggregator.model.UserNewsArticle;
import com.contentaggregator.service.UserNewsArticleService;
import com.google.gson.Gson;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user-news-sources")
public class UserNewsArticleController {
    private final UserNewsArticleService userNewsArticleService;

    public UserNewsArticleController(UserNewsArticleService userNewsArticleService) {
        this.userNewsArticleService = userNewsArticleService;
    }

    @GetMapping("/{userId}")
    public List<UserNewsArticle> getUserNewsSources(@PathVariable int userId) {
        return userNewsArticleService.getUserSources(userId);
    }

    @PostMapping("/{userId}/{sourceId}")
    public ResponseEntity<String> addUserNewsSource(
            @PathVariable int userId,
            @PathVariable int sourceId,
            @RequestBody Map<String, Object> params) {

        Gson gson = new Gson();
        String customParamsJson = gson.toJson(params);  // Convert Map to JSON

        userNewsArticleService.addUserSource(userId, sourceId, customParamsJson);
        return ResponseEntity.ok("News source added successfully.");
    }


}