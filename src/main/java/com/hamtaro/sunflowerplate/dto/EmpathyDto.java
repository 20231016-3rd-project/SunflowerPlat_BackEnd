package com.hamtaro.sunflowerplate.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmpathyDto {

    private Long empathyId;

    private Long memberId;

    private Long reviewId;

}
