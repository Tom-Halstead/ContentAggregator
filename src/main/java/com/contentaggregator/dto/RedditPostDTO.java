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

    @JsonProperty("thumbnail")
    private String thumbnail;

    @JsonProperty("url_overridden_by_dest")
    private String imageUrl;

    /**
     * Returns the full Reddit post URL by appending the permalink to the base URL.
     */
    public String getFullPostUrl() {
        return permalink != null ? "https://www.reddit.com" + permalink : null;
    }

    /**
     * Returns the preferred image URL (imageUrl if available, otherwise thumbnail).
     */
    public String getPreferredImageUrl() {
        return imageUrl != null && !imageUrl.isEmpty() ? imageUrl : thumbnail;
    }

}
