package com.hamtaro.sunflowerplate.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RestaurantSaveDto {
    private String restaurantName;
    private String restaurantTelNum;
    private String restaurantAddress;
    private String restaurantOpenTime;
    private String restaurantBreakTime;
    private String restaurantWebSite;
    private List<RestaurantMenuDto> restaurantMenuDtoList;
//    private List<RestaurantImageDto> restaurantImageDtoList;

    private RestaurantAdministrativeDistrict restaurantAdministrativeDistrict;
}
