package com.contentaggregator.controller;

import com.contentaggregator.model.UserNewsSource;
import com.contentaggregator.service.UserNewsSourceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user-news-source")
public class UserNewsSourceController {
    private final UserNewsSourceService userNewsSourceService;

    public UserNewsSourceController(UserNewsSourceService userNewsSourceService) {
        this.userNewsSourceService = userNewsSourceService;
    }

    @GetMapping("/{userId}")
    public List<UserNewsSource> getUserNewsArticles(@PathVariable int userId) {
        return userNewsSourceService.getUserSources(userId);
    }


}