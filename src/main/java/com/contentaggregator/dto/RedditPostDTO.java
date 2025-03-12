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
    
    // Map to the property that holds the full URL (e.g., "url_overridden_by_dest")
    @JsonProperty("url_overridden_by_dest")
    private String fullPostUrl;

    // Map to thumbnail – note: sometimes thumbnail is a small preview or a text like "self"
    @JsonProperty("thumbnail")
    private String thumbnail;

    // Add a property to determine if the post is a video.
    @JsonProperty("is_video")
    private boolean isVideo;

    // Optionally, map the image/video URL (if available) from "url"
    @JsonProperty("url")
    private String url;

}
