package com.hamtaro.sunflowerplate.repository;

import com.hamtaro.sunflowerplate.entity.restaurant.RestaurantEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity,Long> {
    Optional<RestaurantEntity> findByRestaurantId(Long useId);
    @Query("SELECT r FROM RestaurantEntity r WHERE r.restaurantName LIKE %:keyword%")
//    @Query("SELECT r FROM RestaurantEntity r INNER JOIN r.likeCountEntityList WHERE r.restaurantName LIKE %:keyword%")
    Page<RestaurantEntity> findByRestaurantName(Pageable pageable, @Param("keyword") String keyword);

    @Query("SELECT r FROM RestaurantEntity r WHERE r.dongEntity.dongName LIKE %:add%" +
            " OR r.dongEntity.districtsEntity.districtsName LIKE %:add%" +
            " OR r.dongEntity.districtsEntity.cityEntity.cityName LIKE %:add%")
    Page<RestaurantEntity> findByAddress(Pageable pageable, @Param("add") String add);

}
