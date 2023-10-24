package com.hamtaro.sunflowerplat.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminReportDto {

    private List<ReportDto> reportDtoList;
}
