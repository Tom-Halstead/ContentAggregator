package com.contentaggregator.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RedditPostDTO {

    @JsonProperty("title")
    private String title;

    @JsonProperty("permalink")
    private String permalink;

    private String fullPostUrl;

    @JsonProperty("thumbnail")
    private String thumbnail;


}