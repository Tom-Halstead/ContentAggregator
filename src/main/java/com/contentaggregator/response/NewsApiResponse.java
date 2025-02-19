package com.contentaggregator.response;

import com.contentaggregator.dto.NewsArticleDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class NewsApiResponse {

    private String status;

    @JsonProperty("totalResults")
    private int totalResults;
    @JsonProperty("articles")
    private List<NewsArticleDTO> articles;

}
