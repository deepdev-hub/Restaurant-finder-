package com.jptaxi.application.dto;

import java.math.BigDecimal;
import java.util.List;

import com.jptaxi.application.entity.RestaurantStatus;

public record RestaurantSearchItemDto(
        String id,
        String ownerId,
        String nameVn,
        String nameJp,
        String address,
        String coverImage,
        String openHours,
        BigDecimal avgPrice,
        List<String> tags,
        BigDecimal rating,
        Integer reviewCount,
        RestaurantStatus status,
        BigDecimal lat,
        BigDecimal lng
) {
}
