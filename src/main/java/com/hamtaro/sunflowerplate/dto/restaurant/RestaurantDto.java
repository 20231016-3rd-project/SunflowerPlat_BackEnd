package com.hamtaro.sunflowerplate.dto.restaurant;

import com.hamtaro.sunflowerplate.dto.admin.RestaurantMenuDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class RestaurantDto {

    private Long restaurantId;
    private String restaurantName;
    private String restaurantAddress;
    private String restaurantWebSite;
//    private String ImageUrl;
}
