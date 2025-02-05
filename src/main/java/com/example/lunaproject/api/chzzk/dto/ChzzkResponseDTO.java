package com.example.lunaproject.api.chzzk.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.boot.autoconfigure.web.WebProperties.Resources.Chain.Strategy.Content;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChzzkResponseDTO {
    private int code;
    private String message;
    private Content content;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Content{
        private String channelDescription;
        private boolean verifiedMark;
        private boolean subscriptionAvailability;
        private boolean adMonetizationAvailability;
        private String channelName;
        private String channelType;
        private int followerCount;
        private String channelId;
        private String channelImageUrl;
        private boolean openLive;
    }
}
