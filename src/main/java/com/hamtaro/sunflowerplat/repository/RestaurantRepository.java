package com.hamtaro.sunflowerplat.repository;

import com.hamtaro.sunflowerplat.entity.restaurant.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity,Long> {
    Optional<RestaurantEntity> findByRestaurantId(Long useId);
}
