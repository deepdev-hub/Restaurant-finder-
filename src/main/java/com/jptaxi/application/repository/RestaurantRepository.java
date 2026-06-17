package com.jptaxi.application.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jptaxi.application.entity.RestaurantStatus;
import com.jptaxi.application.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, String> {

    @Query("""
            select
                r.id as id,
                r.owner.id as ownerId,
                r.nameVn as nameVn,
                r.nameJp as nameJp,
                r.address as address,
                r.coverImage as coverImage,
                r.openHours as openHours,
                r.avgPrice as avgPrice,
                r.rating as rating,
                r.reviewCount as reviewCount,
                r.status as status,
                r.lat as lat,
                r.lng as lng
            from Restaurant r
            order by r.rating desc, r.reviewCount desc, coalesce(r.nameJp, r.nameVn) asc
            """)
    List<RestaurantSummaryProjection> findAllSummaries();

    @Query("""
            select
                r.id as id,
                r.owner.id as ownerId,
                r.nameVn as nameVn,
                r.nameJp as nameJp,
                r.address as address,
                r.coverImage as coverImage,
                r.openHours as openHours,
                r.avgPrice as avgPrice,
                r.rating as rating,
                r.reviewCount as reviewCount,
                r.status as status,
                r.lat as lat,
                r.lng as lng
            from Restaurant r
            where r.owner.id = :ownerId
            order by coalesce(r.nameJp, r.nameVn) asc
            """)
    List<RestaurantSummaryProjection> findSummaryByOwnerId(@Param("ownerId") String ownerId);

    @Query("""
            select
                r.id as id,
                r.owner.id as ownerId,
                r.nameVn as nameVn,
                r.nameJp as nameJp,
                r.address as address,
                r.coverImage as coverImage,
                r.openHours as openHours,
                r.avgPrice as avgPrice,
                r.rating as rating,
                r.reviewCount as reviewCount,
                r.status as status,
                r.lat as lat,
                r.lng as lng
            from Restaurant r
            where (:queryLike is null
                    or lower(r.nameVn) like :queryLike
                    or lower(coalesce(r.nameJp, '')) like :queryLike
                    or exists (
                        select 1
                        from RestaurantTag tag
                        where tag.restaurant = r
                          and lower(tag.tagName) like :queryLike
                    )
                    or exists (
                        select 1
                        from MenuItem item
                        where item.restaurant = r
                          and (
                              lower(item.nameVn) like :queryLike
                              or lower(coalesce(item.nameJp, '')) like :queryLike
                          )
                    ))
              and (:openOnly = false or r.status = :openStatus)
              and (:minRating is null or r.rating >= :minRating)
              and (:minAvgPrice is null or r.avgPrice >= :minAvgPrice)
              and (:maxAvgPrice is null or r.avgPrice <= :maxAvgPrice)
              and (:hasTags = false or exists (
                    select 1
                    from RestaurantTag filterTag
                    where filterTag.restaurant = r
                      and lower(filterTag.tagName) in :normalizedTags
              ))
            order by r.rating desc, r.reviewCount desc, coalesce(r.nameJp, r.nameVn) asc
            """)
    List<RestaurantSummaryProjection> searchSummaries(
            @Param("queryLike") String queryLike,
            @Param("openOnly") boolean openOnly,
            @Param("openStatus") RestaurantStatus openStatus,
            @Param("minRating") BigDecimal minRating,
            @Param("minAvgPrice") BigDecimal minAvgPrice,
            @Param("maxAvgPrice") BigDecimal maxAvgPrice,
            @Param("hasTags") boolean hasTags,
            @Param("normalizedTags") List<String> normalizedTags,
            Pageable pageable
    );
}
