package com.hamtaro.sunflowerplat.repository;

import com.hamtaro.sunflowerplat.entity.review.RequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<RequestEntity,Long>{
}
