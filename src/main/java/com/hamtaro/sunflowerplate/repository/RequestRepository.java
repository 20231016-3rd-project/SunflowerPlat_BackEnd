package com.hamtaro.sunflowerplate.repository;

import com.hamtaro.sunflowerplate.entity.review.RequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<RequestEntity,Long>{
}
