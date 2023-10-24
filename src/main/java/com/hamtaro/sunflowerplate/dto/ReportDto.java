package com.hamtaro.sunflowerplate.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportDto {

    private Long reviewId;

    private Long memberId;

    private String reportCategory;

    private String reportContent;

    private LocalDate reportAt;
}
