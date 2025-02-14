package com.contentaggregator.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_news_source")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(UserNewsSourceId.class)  // Composite Key Handling
public class UserNewsSource {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "news_source_id", nullable = false)
    private NewsSource newsSource;

    @Column(name = "custom_parameters", columnDefinition = "jsonb")
    private String customParameters;  // JSON parameters for filtering

    @Column(name = "added_on", nullable = false, updatable = false)
    private LocalDateTime addedOn = LocalDateTime.now();
}