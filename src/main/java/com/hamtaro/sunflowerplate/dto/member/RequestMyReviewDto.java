package com.hamtaro.sunflowerplate.dto.member;

import com.hamtaro.sunflowerplate.dto.review.ReviewImageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestMyReviewDto {

    private Long restaurantId;

    private String restaurantName;

    private String reviewContent;

    private Integer reviewStarRating;

    private List<ReviewImageDto> reviewImageDto;

    private LocalDate reviewAt;
}
