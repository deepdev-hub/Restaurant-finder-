package com.jptaxi.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import com.jptaxi.application.dto.RestaurantSearchItemDto;
import com.jptaxi.application.entity.RestaurantStatus;
import com.jptaxi.application.repository.RestaurantRepository;
import com.jptaxi.application.repository.RestaurantSummaryProjection;
import com.jptaxi.application.repository.RestaurantTagProjection;
import com.jptaxi.application.repository.RestaurantTagRepository;

class RestaurantQueryServiceTests {

    @Test
    void searchCapsLimitNormalizesQueryAndBuildsSummaryDtos() {
        RestaurantRepository restaurantRepository = mock(RestaurantRepository.class);
        RestaurantTagRepository tagRepository = mock(RestaurantTagRepository.class);
        RestaurantQueryService service = new RestaurantQueryService(restaurantRepository, tagRepository);
        when(restaurantRepository.searchSummaries(
                eq("%pho%"),
                eq(true),
                eq(new BigDecimal("4.0")),
                eq(new BigDecimal("50000")),
                eq(new BigDecimal("100000")),
                eq(true),
                eq(List.of("pho", "bun cha")),
                any(Pageable.class)
        )).thenReturn(List.of(summary("r1", "u1", "Pho Bac")));
        when(tagRepository.findTagSummariesByRestaurantIds(List.of("r1")))
                .thenReturn(List.of(tag("r1", "Pho"), tag("r1", "Bun cha")));

        List<RestaurantSearchItemDto> items = service.searchRestaurants(
                "  Pho  ",
                List.of("Pho", "Bun cha", "Pho"),
                true,
                new BigDecimal("4.0"),
                new BigDecimal("50000"),
                new BigDecimal("100000"),
                20
        );

        assertThat(items).hasSize(1);
        assertThat(items.get(0).tags()).containsExactly("Pho", "Bun cha");
        verify(restaurantRepository).searchSummaries(
                eq("%pho%"),
                eq(true),
                eq(new BigDecimal("4.0")),
                eq(new BigDecimal("50000")),
                eq(new BigDecimal("100000")),
                eq(true),
                eq(List.of("pho", "bun cha")),
                eq(Pageable.ofSize(5))
        );
    }

    @Test
    void ownerSummariesLoadTagsInOneBatch() {
        RestaurantRepository restaurantRepository = mock(RestaurantRepository.class);
        RestaurantTagRepository tagRepository = mock(RestaurantTagRepository.class);
        RestaurantQueryService service = new RestaurantQueryService(restaurantRepository, tagRepository);
        when(restaurantRepository.findSummaryByOwnerId("owner-1"))
                .thenReturn(List.of(summary("r1", "owner-1", "Pho Bac"), summary("r2", "owner-1", "Bun Cha")));
        when(tagRepository.findTagSummariesByRestaurantIds(List.of("r1", "r2")))
                .thenReturn(List.of(tag("r1", "Pho"), tag("r2", "Bun cha"), tag("r2", "Truyen thong")));

        List<RestaurantSearchItemDto> items = service.getRestaurantSummaries("owner-1");

        assertThat(items).hasSize(2);
        assertThat(items.get(0).tags()).containsExactly("Pho");
        assertThat(items.get(1).tags()).containsExactly("Bun cha", "Truyen thong");
        verify(tagRepository).findTagSummariesByRestaurantIds(List.of("r1", "r2"));
    }

    private RestaurantSummaryProjection summary(String id, String ownerId, String nameJp) {
        return new RestaurantSummaryProjection() {
            @Override
            public String getId() {
                return id;
            }

            @Override
            public String getOwnerId() {
                return ownerId;
            }

            @Override
            public String getNameVn() {
                return nameJp;
            }

            @Override
            public String getNameJp() {
                return nameJp;
            }

            @Override
            public String getAddress() {
                return "Ha Noi";
            }

            @Override
            public String getCoverImage() {
                return "https://example.com/" + id + ".jpg";
            }

            @Override
            public String getOpenHours() {
                return "10:00 - 20:00";
            }

            @Override
            public BigDecimal getAvgPrice() {
                return new BigDecimal("75000");
            }

            @Override
            public BigDecimal getRating() {
                return new BigDecimal("4.5");
            }

            @Override
            public Integer getReviewCount() {
                return 12;
            }

            @Override
            public RestaurantStatus getStatus() {
                return RestaurantStatus.open;
            }

            @Override
            public BigDecimal getLat() {
                return new BigDecimal("21.027764");
            }

            @Override
            public BigDecimal getLng() {
                return new BigDecimal("105.834160");
            }
        };
    }

    private RestaurantTagProjection tag(String restaurantId, String tagName) {
        return new RestaurantTagProjection() {
            @Override
            public String getRestaurantId() {
                return restaurantId;
            }

            @Override
            public String getTagName() {
                return tagName;
            }
        };
    }
}
