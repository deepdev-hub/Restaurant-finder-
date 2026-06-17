package com.jptaxi.application.dto;

import java.time.ZonedDateTime;
import java.util.List;

public record ConversationDto(
        String id,
        List<String> participants,
        String lastMessage,
        ZonedDateTime lastTimestamp,
        String restaurantId,
        String restaurantName
) {
}
