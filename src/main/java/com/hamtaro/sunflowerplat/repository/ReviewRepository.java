package com.hamtaro.sunflowerplat.repository;

import com.hamtaro.sunflowerplat.entity.review.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {


}
