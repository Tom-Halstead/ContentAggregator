package com.contentaggregator.service;

import com.contentaggregator.model.NewsArticle;
import com.contentaggregator.model.User;
import com.contentaggregator.model.UserNewsArticle;
import com.contentaggregator.repository.NewsArticleRepository;
import com.contentaggregator.repository.UserNewsArticleRepository;
import com.contentaggregator.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserNewsArticleService {
    private final UserNewsArticleRepository userNewsArticleRepository;
    private final UserRepository userRepository;
    private final NewsArticleRepository newsArticleRepository;

    public UserNewsArticleService(UserNewsArticleRepository userNewsArticleRepository, UserRepository userRepository, NewsArticleRepository newsArticleRepository) {
        this.userNewsArticleRepository = userNewsArticleRepository;
        this.userRepository = userRepository;
        this.newsArticleRepository = newsArticleRepository;
    }

    public List<UserNewsArticle> getUserSources(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return userNewsArticleRepository.findByUser(user);
    }

    public void addUserSource(int userId, int sourceId, String customParams) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        NewsArticle newsArticle = newsArticleRepository.findById(sourceId)
                .orElseThrow(() -> new RuntimeException("News source not found"));

        UserNewsArticle userNewsArticle = new UserNewsArticle(user, newsArticle, customParams, LocalDateTime.now());
        userNewsArticleRepository.save(userNewsArticle);
    }
}
