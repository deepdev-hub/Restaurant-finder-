package com.jptaxi.application.dto;

import java.time.ZonedDateTime;

public record MessageDto(
        String id,
        String senderId,
        String receiverId,
        String restaurantId,
        String content,
        ZonedDateTime timestamp,
        Boolean read
) {
}
