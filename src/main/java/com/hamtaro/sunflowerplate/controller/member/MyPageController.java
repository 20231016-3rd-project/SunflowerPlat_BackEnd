package com.hamtaro.sunflowerplate.controller.member;

import com.hamtaro.sunflowerplate.dto.member.UpdateReviewDto;
import com.hamtaro.sunflowerplate.jwt.config.TokenProvider;
import com.hamtaro.sunflowerplate.service.member.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> deleteMyReview(HttpServletRequest request, @RequestParam Long reviewId) {
        String header = request.getHeader(tokenProvider.loginAccessToken);
        String userId = tokenProvider.getUserPk(header);
        return myPageService.deleteMyReview(reviewId, userId);
    }

    @PutMapping("/myreview")
    public ResponseEntity<?> updateMyReview(HttpServletRequest request, @RequestParam Long reviewId, @RequestPart UpdateReviewDto updateReviewDto, @RequestPart List<MultipartFile> imageFile) {
        String header = request.getHeader(tokenProvider.loginAccessToken);
        String userId = tokenProvider.getUserPk(header);
        Boolean check = myPageService.updateMyReview(reviewId, updateReviewDto, userId, imageFile);
        if (check) {
            return myPageService.updateMyReview(reviewId);
//            return null;
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "권한없음");
            return ResponseEntity.status(403).body(response);

        }
    }

    @GetMapping("/myplace")
    public ResponseEntity<?> getMyPlace(HttpServletRequest request) {
        String header = request.getHeader(tokenProvider.loginAccessToken);
        String userId = tokenProvider.getUserPk(header);
        return myPageService.getMyPlace(userId);
    }
}
