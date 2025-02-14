package com.contentaggregator.model;


import jakarta.persistence.*;
import jdk.jfr.DataAmount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "news_source")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsSource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_source_id")
    private int id;

    @Column(nullable = false)
    private String name;  // e.g., "Reddit News", "CNN"

    @Column(name = "api_type")
    private String apiType;  // e.g., "Reddit", "NewsAPI"

    @Column(nullable = false)
    private String url;  // API endpoint or base URL

    @Column
    private String category;  // e.g., "technology", "world"
}
