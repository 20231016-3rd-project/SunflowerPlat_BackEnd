package com.hamtaro.sunflowerplate.controller;

import com.hamtaro.sunflowerplate.dto.restaurant.RestaurantDto;
import com.hamtaro.sunflowerplate.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/sunflowerPlate/restaurant")
public class RestaurantController {
    private final RestaurantService restaurantService;

    @GetMapping("/{restaurantId}")
    public ResponseEntity<?> findRestaurantInfo(@PathVariable Long restaurantId){
        return restaurantService.findRestaurantDetailsById(restaurantId);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<RestaurantDto>> findRestaurantList (
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page ){
        return restaurantService.findRestaurantByKeyword(keyword, page-1);
    }

    @GetMapping("/address")
    public ResponseEntity<Page<RestaurantDto>> findRestaurantListByAddress (
            @RequestParam String add,
            @RequestParam(defaultValue = "1") int page ){
        return restaurantService.findRestaurantByAddress(add,page-1);
    }

}