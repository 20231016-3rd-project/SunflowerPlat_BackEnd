package com.hamtaro.sunflowerplate.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestMyPlaceDto {

    private Long restaurantId;
    private String restaurantName;
    private String restaurantAddress;
    private String restaurantWebSite;
    private String resizeImgUrl;
}
