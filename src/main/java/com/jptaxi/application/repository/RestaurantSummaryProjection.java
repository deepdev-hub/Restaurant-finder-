package com.jptaxi.application.repository;

import java.math.BigDecimal;

import com.jptaxi.application.entity.RestaurantStatus;

public interface RestaurantSummaryProjection {

    String getId();

    String getOwnerId();

    String getNameVn();

    String getNameJp();

    String getAddress();

    String getCoverImage();

    String getOpenHours();

    BigDecimal getAvgPrice();

    BigDecimal getRating();

    Integer getReviewCount();

    RestaurantStatus getStatus();

    BigDecimal getLat();

    BigDecimal getLng();
}
