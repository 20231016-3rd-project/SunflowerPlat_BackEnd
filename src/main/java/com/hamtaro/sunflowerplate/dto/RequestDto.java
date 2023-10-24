package com.hamtaro.sunflowerplate.dto;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {

    private Long requestId;

    private String requestContent; //요청 내용

    private LocalDate requestAt; // 요청 날짜

}
