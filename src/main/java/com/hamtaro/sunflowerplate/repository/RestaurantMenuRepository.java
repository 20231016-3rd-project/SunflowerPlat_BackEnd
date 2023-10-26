package com.hamtaro.sunflowerplate.repository;

import com.hamtaro.sunflowerplate.entity.restaurant.RestaurantMenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantMenuRepository extends JpaRepository<RestaurantMenuEntity,Long> {
}
