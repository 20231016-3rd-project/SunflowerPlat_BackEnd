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

    // 검색1. 키워드만 입력
//    @Query("SELECT r FROM RestaurantEntity r WHERE r.restaurantName LIKE %:keyword%")
    @Query("SELECT r FROM RestaurantEntity r INNER JOIN r.likeCountEntityList WHERE r.restaurantName LIKE %:keyword%")
    Page<RestaurantEntity> findByRestaurantName(Pageable pageable, @Param("keyword") String keyword);

    // 검색2. 키워드, dong 입력
    @Query("SELECT r FROM RestaurantEntity r WHERE r.restaurantName LIKE %:keyword% " +
            "AND r.dongEntity.dongName = :dong")
    Page<RestaurantEntity> findByRestaurantNameAndDongEntity_DongName(Pageable pageable, @Param("keyword") String keyword, String dong);

    // 검색3. 키워드, district 입력
    @Query("SELECT r FROM RestaurantEntity r WHERE r.restaurantName LIKE %:keyword% " +
            "AND r.dongEntity.districtsEntity.districtsName = :district")
    Page<RestaurantEntity> findByRestaurantNameAndDongEntity_DistrictsEntity_DistrictsName(Pageable pageable, @Param("keyword") String keyword, String district);

    // 검색4. 키워드, city 입력
    @Query("SELECT r FROM RestaurantEntity r WHERE r.restaurantName LIKE %:keyword% " +
            "AND r.dongEntity.districtsEntity.cityEntity.cityName = :city")
    Page<RestaurantEntity> findByRestaurantNameAndDongEntity_DistrictsEntity_CityEntity_CityName(Pageable pageable, @Param("keyword") String keyword, String city);

    @Query("SELECT r FROM RestaurantEntity r WHERE r.dongEntity.dongName LIKE %:add%" +
            " OR r.dongEntity.districtsEntity.districtsName LIKE %:add%" +
            " OR r.dongEntity.districtsEntity.cityEntity.cityName LIKE %:add%")
    Page<RestaurantEntity> findByAddress(Pageable pageable, @Param("add") String add);

}
