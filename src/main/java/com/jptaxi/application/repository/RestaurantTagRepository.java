package com.jptaxi.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jptaxi.application.entity.RestaurantTag;

public interface RestaurantTagRepository extends JpaRepository<RestaurantTag, Long> {

    @Query("select distinct tag.tagName from RestaurantTag tag order by tag.tagName")
    List<String> findDistinctTagNames();

    @Query("""
            select tag.restaurant.id as restaurantId, tag.tagName as tagName
            from RestaurantTag tag
            where tag.restaurant.id in :restaurantIds
            order by tag.restaurant.id, tag.tagName
            """)
    List<RestaurantTagProjection> findTagSummariesByRestaurantIds(@Param("restaurantIds") List<String> restaurantIds);
}
