package com.contentaggregator.dto;

import lombok.Data;
import lombok.Getter;


@Getter
@Data
public class ArticleDTO {

    private SourceDTO source;
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;
    private String content;
}
