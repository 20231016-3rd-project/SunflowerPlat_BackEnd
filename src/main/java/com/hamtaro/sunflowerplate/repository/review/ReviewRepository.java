package com.hamtaro.sunflowerplate.repository.review;

import com.hamtaro.sunflowerplate.entity.review.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    Optional<ReviewEntity> findByReviewId(Long reviewId);
    List<ReviewEntity> findByMemberEntity_MemberId(Long id);

    List<ReviewEntity> findReviewEntityByReviewImageEntityList(Long reviewId);
}
