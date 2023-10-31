package com.hamtaro.sunflowerplate.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateReviewDto {

    private String reviewContent;

    private List<UpdateReviewImageDto> imageDtoList;



}
