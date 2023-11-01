package com.hamtaro.sunflowerplate.controller.restaurant;

import com.hamtaro.sunflowerplate.dto.restaurant.RestaurantDto;
import com.hamtaro.sunflowerplate.service.restaurant.RestaurantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/sunflowerPlate/restaurant")
@Tag(name = "식당", description = "식당 관련 API")
public class RestaurantController {
    private final RestaurantService restaurantService;

    @GetMapping("/{restaurantId}")
    public ResponseEntity<?> findRestaurantInfo(@PathVariable Long restaurantId,
                                                @RequestParam(defaultValue = "1") int reviewPage){
        return restaurantService.findRestaurantDetailsById(restaurantId, reviewPage-1);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<RestaurantDto>> findRestaurantList (
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "rateDesc") String sort,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String dong,
            @RequestParam(defaultValue = "1") int page ){
        return restaurantService.findRestaurantByKeyword(page-1, sort, keyword, city, district, dong);
    }

    @GetMapping("/address")
    public ResponseEntity<Page<RestaurantDto>> findRestaurantListByAddress (
            @RequestParam String add,
            @RequestParam(defaultValue = "1") int page ){
        return restaurantService.findRestaurantByAddress(add,page-1);
    }

}