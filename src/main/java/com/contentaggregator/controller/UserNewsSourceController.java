package com.contentaggregator.controller;

import com.contentaggregator.model.UserNewsSource;
import com.contentaggregator.service.UserNewsSourceService;
import com.google.gson.Gson;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user-news-sources")
public class UserNewsSourceController {
    private final UserNewsSourceService userNewsSourceService;

    public UserNewsSourceController(UserNewsSourceService userNewsSourceService) {
        this.userNewsSourceService = userNewsSourceService;
    }

    @GetMapping("/{userId}")
    public List<UserNewsSource> getUserNewsSources(@PathVariable int userId) {
        return userNewsSourceService.getUserSources(userId);
    }

    @PostMapping("/{userId}/{sourceId}")
    public ResponseEntity<String> addUserNewsSource(
            @PathVariable int userId,
            @PathVariable int sourceId,
            @RequestBody Map<String, Object> params) {

        Gson gson = new Gson();
        String customParamsJson = gson.toJson(params);  // Convert Map to JSON

        userNewsSourceService.addUserSource(userId, sourceId, customParamsJson);
        return ResponseEntity.ok("News source added successfully.");
    }


}