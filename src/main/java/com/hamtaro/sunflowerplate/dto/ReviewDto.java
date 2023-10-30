package com.hamtaro.sunflowerplate.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReviewDto {

    private Long restaurantId;

    private Long reviewId;

    private Long memberId;

    private String reviewContent;

    private Integer reviewStarRating;

    private LocalDate reviewAt;
}
