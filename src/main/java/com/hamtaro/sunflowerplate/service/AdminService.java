package com.hamtaro.sunflowerplate.service;

import com.hamtaro.sunflowerplate.entity.review.ReviewEntity;
import com.hamtaro.sunflowerplate.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class AdminService {
    private final ReviewRepository reviewRepository;

    public ResponseEntity<?> deleteAdminReview(Long reviewId){
        Map<String, String> result = new HashMap<>();
        Optional<ReviewEntity> deleteId = reviewRepository.findByReviewId(reviewId);
        if (deleteId.isPresent()){
            reviewRepository.deleteByReviewId(reviewId);
            result.put("message","리뷰가 삭제되었습니다.");
            return ResponseEntity.status(200).body(result);
        }else {
            result.put("message","권한이 없습니다.");
            return ResponseEntity.status(403).body(result);
        }

    }
}
