package com.hamtaro.sunflowerplate.controller.member;

import com.hamtaro.sunflowerplate.dto.member.UpdateReviewDto;
import com.hamtaro.sunflowerplate.jwt.config.TokenProvider;
import com.hamtaro.sunflowerplate.service.member.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sunflowerPlate/mypage")
public class MyPageController {

    private final MyPageService myPageService;
    private final TokenProvider tokenProvider;

    @GetMapping("/myreview")
    public ResponseEntity<?> getMyReview(HttpServletRequest request) {
        String header = request.getHeader(tokenProvider.loginAccessToken);
        String userId = tokenProvider.getUserPk(header);
        return myPageService.getMyReview(userId);
    }

    @DeleteMapping("/myreview")
    public ResponseEntity<?> deleteMyReview(@RequestParam Long reviewId){
        return myPageService.deleteMyReview(reviewId);
    }

    @PutMapping("/myreview")
    public ResponseEntity<?> updateMyReview(@RequestParam Long reviewId, @RequestPart UpdateReviewDto updateReviewDto) {
        return myPageService.updateMyReview(reviewId, updateReviewDto);
    }

    @GetMapping("/myplace")
    public ResponseEntity<?> getMyPlace(HttpServletRequest request) {
        String header = request.getHeader(tokenProvider.loginAccessToken);
        String userId = tokenProvider.getUserPk(header);
        return myPageService.getMyPlace(userId);
    }
}
