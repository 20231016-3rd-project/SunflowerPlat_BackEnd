package com.hamtaro.sunflowerplate.dto.review;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewReturnDto {
    private Long reviewId;

    private Long memberId;

    private String reviewContent;

    private Integer reviewStarRating;

    private LocalDate reviewAt;

    private List<ReviewImageDto> reviewImageDtoList;
    private int reviewEmpathyCount;
    private boolean empathyReview;
}
