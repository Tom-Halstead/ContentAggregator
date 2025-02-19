package com.contentaggregator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NewsSourceDTO {

    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
}
