package com.hamtaro.sunflowerplat.dto;

import com.hamtaro.sunflowerplat.entity.member.MemberEntity;
import lombok.*;
import org.springframework.http.RequestEntity;

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
