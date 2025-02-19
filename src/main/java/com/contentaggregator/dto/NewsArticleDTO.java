package com.contentaggregator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;


@Getter
@Data
public class NewsArticleDTO {

    @JsonProperty("source")
    private NewsSourceDTO source;
    @JsonProperty("author")
    private String author;
    @JsonProperty("title")
    private String title;
    @JsonProperty("source")
    private String description;
    @JsonProperty("url")
    private String url;
    @JsonProperty("urlToImage")
    private String urlToImage;
    @JsonProperty("publishedAt")
    private String publishedAt;
    @JsonProperty("content")
    private String content;
}
