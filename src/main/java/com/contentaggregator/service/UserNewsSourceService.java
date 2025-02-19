package com.contentaggregator.service;

import com.contentaggregator.model.NewsSource;
import com.contentaggregator.model.User;
import com.contentaggregator.model.UserNewsSource;
import com.contentaggregator.repository.NewsSourceRepository;
import com.contentaggregator.repository.UserNewsSourceRepository;
import com.contentaggregator.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserNewsSourceService {
    private final UserNewsSourceRepository userNewsSourceRepository;
    private final UserRepository userRepository;
    private final NewsSourceRepository newsSourceRepository;

    public UserNewsSourceService(UserNewsSourceRepository userNewsSourceRepository, UserRepository userRepository, NewsSourceRepository newsSourceRepository) {
        this.userNewsSourceRepository = userNewsSourceRepository;
        this.userRepository = userRepository;
        this.newsSourceRepository = newsSourceRepository;
    }

    public List<UserNewsSource> getUserSources(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return userNewsSourceRepository.findByUser(user);
    }

    public void addUserSource(int userId, int sourceId, String customParams) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        NewsSource newsSource = newsSourceRepository.findById(sourceId)
                .orElseThrow(() -> new RuntimeException("News source not found"));

        UserNewsSource userNewsSource = new UserNewsSource(user, newsSource, customParams, LocalDateTime.now());
        userNewsSourceRepository.save(userNewsSource);
    }
}
