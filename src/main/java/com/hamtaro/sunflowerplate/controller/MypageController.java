package com.hamtaro.sunflowerplate.controller;

import com.hamtaro.sunflowerplate.service.restaurant.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/sunflowerPlate/mypage")
public class MypageController {
    private final MypageService mypageService;

    @GetMapping("/myplace")
    public ResponseEntity<?> clickLike(@RequestParam Long restaurantId, @RequestParam Long memberId){
        return mypageService.clickLikeButton(restaurantId,memberId);
    }


}
