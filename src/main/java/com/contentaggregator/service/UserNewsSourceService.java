package com.contentaggregator.service;

import com.contentaggregator.exception.ResourceNotFoundException;
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

    /**
     * Fetches a list of news sources associated with a user.
     *
     * @param userId The ID of the user.
     * @return List of UserNewsSource objects.
     * @throws ResourceNotFoundException if the user does not exist.
     */
    public List<UserNewsSource> getUserSources(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        return userNewsSourceRepository.findByUser(user);
    }

    /**
     * Adds a new news source for a user.
     *
     * @param userId       The ID of the user.
     * @param sourceId     The ID of the news source.
     * @param customParams Any custom parameters for the user-specific source configuration.
     * @throws ResourceNotFoundException if the user or news source does not exist.
     */
    public void addUserSource(int userId, int sourceId, String customParams) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        NewsSource newsSource = newsSourceRepository.findById(sourceId)
                .orElseThrow(() -> new ResourceNotFoundException("News source not found with ID: " + sourceId));

        UserNewsSource userNewsSource = new UserNewsSource(user, newsSource, customParams, LocalDateTime.now());

        try {
            userNewsSourceRepository.save(userNewsSource);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save user news source", e);
        }
    }
}
