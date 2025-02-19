package com.contentaggregator.response;

import com.contentaggregator.model.NewsArticle;
import lombok.Data;

import java.util.List;

@Data
public class NewsApiResponse {

    private String status;
    private int totalResults;
    private List<NewsArticle> articleList;

}
