package com.hamtaro.sunflowerplate.dto.restaurant;

import com.hamtaro.sunflowerplate.dto.admin.RestaurantMenuDto;
import com.hamtaro.sunflowerplate.dto.review.ReviewReturnDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
public class RestaurantDetailDto {
    private String restaurantName;
    private String restaurantTelNum;
    private String restaurantAddress;
    private String restaurantOpenTime;
    private String restaurantBreakTime;
    private String restaurantWebSite;
    private RestaurantLikeCountDto restaurantLikeCountDto;
    private List<RestaurantMenuDto> restaurantMenuDtoList;
    private List<RestaurantImageDto> restaurantImageDtoList;
    private Page<ReviewReturnDto> reviewReturnDtoPage;
}