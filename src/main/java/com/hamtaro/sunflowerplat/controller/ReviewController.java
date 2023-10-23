package com.hamtaro.sunflowerplat.controller;

import com.hamtaro.sunflowerplat.dto.ReviewDto;
import com.hamtaro.sunflowerplat.dto.ReviewSaveDto;
import com.hamtaro.sunflowerplat.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/sunflowerPlate/user")
public class ReviewController {
    private final ReviewService reviewService;

    //리뷰 작성 후 저장
    @PostMapping("/review/new")
    public ResponseEntity<?> createReview(@RequestBody ReviewSaveDto reviewSaveDto, @RequestParam Long restaurantId){
        return reviewService.saveUserReview(reviewSaveDto, restaurantId);
    }

}
