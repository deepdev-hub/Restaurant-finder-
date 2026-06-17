package com.jptaxi.application.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jptaxi.application.dto.RestaurantSearchItemDto;
import com.jptaxi.application.repository.RestaurantRepository;
import com.jptaxi.application.repository.RestaurantSummaryProjection;
import com.jptaxi.application.repository.RestaurantTagProjection;
import com.jptaxi.application.repository.RestaurantTagRepository;

@Service
public class RestaurantQueryService {

    private static final int DEFAULT_SEARCH_LIMIT = 20;
    private static final int MAX_SEARCH_LIMIT = 20;

    private final RestaurantRepository restaurantRepository;
    private final RestaurantTagRepository restaurantTagRepository;

    public RestaurantQueryService(
            RestaurantRepository restaurantRepository,
            RestaurantTagRepository restaurantTagRepository
    ) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantTagRepository = restaurantTagRepository;
    }

    @Transactional(readOnly = true)
    public List<RestaurantSearchItemDto> getRestaurantSummaries(String ownerId) {
        List<RestaurantSummaryProjection> rows = (ownerId == null || ownerId.isBlank())
                ? restaurantRepository.findAllSummaries()
                : restaurantRepository.findSummaryByOwnerId(ownerId.trim());
        return toDtos(rows);
    }

    @Transactional(readOnly = true)
    public List<RestaurantSearchItemDto> searchRestaurants(
            String query,
            List<String> tags,
            boolean openOnly,
            BigDecimal minRating,
            BigDecimal minAvgPrice,
            BigDecimal maxAvgPrice,
            Integer limit
    ) {
        List<String> normalizedTags = normalizeTags(tags);
        String normalizedQuery = normalizeQuery(query);
        int resolvedLimit = resolveLimit(limit);
        List<RestaurantSummaryProjection> rows = restaurantRepository.searchSummaries(
                normalizedQuery == null ? null : "%" + normalizedQuery + "%",
                openOnly,
                minRating,
                minAvgPrice,
                maxAvgPrice,
                !normalizedTags.isEmpty(),
                normalizedTags.isEmpty() ? List.of("__unused__") : normalizedTags,
                PageRequest.of(0, resolvedLimit)
        );
        return toDtos(rows);
    }

    private List<RestaurantSearchItemDto> toDtos(List<RestaurantSummaryProjection> rows) {
        if (rows.isEmpty()) return List.of();

        List<String> restaurantIds = rows.stream()
                .map(RestaurantSummaryProjection::getId)
                .toList();
        Map<String, List<String>> tagsByRestaurantId = buildTagMap(
                restaurantTagRepository.findTagSummariesByRestaurantIds(restaurantIds)
        );

        List<RestaurantSearchItemDto> items = new ArrayList<>(rows.size());
        for (RestaurantSummaryProjection row : rows) {
            items.add(new RestaurantSearchItemDto(
                    row.getId(),
                    row.getOwnerId(),
                    row.getNameVn(),
                    row.getNameJp(),
                    row.getAddress(),
                    row.getCoverImage(),
                    row.getOpenHours(),
                    row.getAvgPrice(),
                    tagsByRestaurantId.getOrDefault(row.getId(), List.of()),
                    row.getRating(),
                    row.getReviewCount(),
                    row.getStatus(),
                    row.getLat(),
                    row.getLng()
            ));
        }
        return items;
    }

    private Map<String, List<String>> buildTagMap(List<RestaurantTagProjection> tagRows) {
        Map<String, List<String>> tagsByRestaurantId = new LinkedHashMap<>();
        for (RestaurantTagProjection row : tagRows) {
            tagsByRestaurantId.computeIfAbsent(row.getRestaurantId(), ignored -> new ArrayList<>())
                    .add(row.getTagName());
        }
        return tagsByRestaurantId;
    }

    private List<String> normalizeTags(List<String> tags) {
        if (tags == null) return List.of();
        return tags.stream()
                .filter(tag -> tag != null && !tag.isBlank())
                .map(tag -> tag.trim().toLowerCase(Locale.ROOT))
                .distinct()
                .toList();
    }

    private String normalizeQuery(String query) {
        if (query == null || query.isBlank()) return null;
        return query.trim().toLowerCase(Locale.ROOT);
    }

    private int resolveLimit(Integer limit) {
        if (limit == null || limit <= 0) return DEFAULT_SEARCH_LIMIT;
        return Math.min(limit, MAX_SEARCH_LIMIT);
    }
}
