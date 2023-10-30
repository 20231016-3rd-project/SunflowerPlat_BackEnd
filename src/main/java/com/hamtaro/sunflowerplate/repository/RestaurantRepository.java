package com.hamtaro.sunflowerplate.repository;

import com.hamtaro.sunflowerplate.entity.restaurant.RestaurantEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity,Long> {
    Optional<RestaurantEntity> findByRestaurantId(Long useId);

    // 리뷰 많은 순 정렬
    // 1. 키워드 입력
    @Query("SELECT r FROM RestaurantEntity r " +
            "WHERE r.restaurantName LIKE %:keyword% " +
            "AND r.restaurantStatus = 'OPEN'")
    Page<RestaurantEntity> findByRestaurantName(Pageable pageable, @Param("keyword") String keyword);

    // 2. 키워드, dong 입력
    @Query("SELECT r FROM RestaurantEntity r " +
            "WHERE r.restaurantName LIKE %:keyword% " +
            "AND r.dongEntity.dongName = :dong " +
            "AND r.restaurantStatus = 'OPEN'")
    Page<RestaurantEntity> findByRestaurantNameAndDongEntity_DongName(Pageable pageable, @Param("keyword") String keyword, String dong);

    // 3. 키워드, district 입력
    @Query("SELECT r FROM RestaurantEntity r " +
            "WHERE r.restaurantName LIKE %:keyword% " +
            "AND r.dongEntity.districtsEntity.districtsName = :district " +
            "AND r.restaurantStatus = 'OPEN'")
    Page<RestaurantEntity> findByRestaurantNameAndDongEntity_DistrictsEntity_DistrictsName(Pageable pageable, @Param("keyword") String keyword, String district);

    // 4. 키워드, city 입력
    @Query("SELECT r FROM RestaurantEntity r " +
            "WHERE r.restaurantName LIKE %:keyword% " +
            "AND r.dongEntity.districtsEntity.cityEntity.cityName = :city " +
            "AND r.restaurantStatus = 'OPEN'")
    Page<RestaurantEntity> findByRestaurantNameAndDongEntity_DistrictsEntity_CityEntity_CityName(Pageable pageable, @Param("keyword") String keyword, String city);

    // 좋아요 많은 순 정렬
    // 1. 키워드 입력
    @Query("SELECT r FROM RestaurantEntity r " +
            "LEFT JOIN LikeCountEntity l ON r.restaurantId = l.restaurantEntity.restaurantId " +
            "WHERE r.restaurantName LIKE %:keyword% " +
            "AND r.restaurantStatus = 'OPEN' " +
            "GROUP BY r.restaurantId " +
            "ORDER BY SUM(CASE WHEN l.likeStatus = true THEN 1 ELSE 0 END) DESC ")
    Page<RestaurantEntity> findByRestaurantNameAndLikeCountEntity_likeStatus(Pageable pageable, @Param("keyword") String keyword);

    // 2. 키워드, dong 입력
    @Query("SELECT r FROM RestaurantEntity r " +
            "LEFT JOIN LikeCountEntity l ON r.restaurantId = l.restaurantEntity.restaurantId " +
            "WHERE r.restaurantName LIKE %:keyword% " +
            "AND r.dongEntity.dongName = :dong " +
            "AND r.restaurantStatus = 'OPEN' " +
            "GROUP BY r.restaurantId " +
            "ORDER BY SUM(CASE WHEN l.likeStatus = true THEN 1 ELSE 0 END) DESC ")
    Page<RestaurantEntity> findByRestaurantNameAndDongEntity_DongNameAndLikeCountEntity_likeStatus(Pageable pageable, @Param("keyword") String keyword, String dong);

    // 3. 키워드, district 입력
    @Query ("SELECT r FROM RestaurantEntity r " +
            "LEFT JOIN LikeCountEntity l ON r.restaurantId = l.restaurantEntity.restaurantId " +
            "WHERE r.restaurantName LIKE %:keyword% " +
            "AND r.dongEntity.districtsEntity.districtsName = :district " +
            "AND r.restaurantStatus = 'OPEN'" +
            "GROUP BY r.restaurantId " +
            "ORDER BY SUM(CASE WHEN l.likeStatus = true THEN 1 ELSE 0 END) DESC")
    Page<RestaurantEntity> findByRestaurantNameAndDongEntity_DistrictsEntity_DistrictsNameAndLikeCountEntity_likeStatus(Pageable pageable, @Param("keyword") String keyword, String district);

    // 4. 키워드, city 입력
    @Query("SELECT r FROM RestaurantEntity r " +
            "LEFT JOIN LikeCountEntity l ON r.restaurantId = l.restaurantEntity.restaurantId " +
            "WHERE r.restaurantName LIKE %:keyword% " +
            "AND r.dongEntity.districtsEntity.cityEntity.cityName = :city " +
            "AND r.restaurantStatus = 'OPEN'"+
            "GROUP BY r.restaurantId " +
            "ORDER BY SUM(CASE WHEN l.likeStatus = true THEN 1 ELSE 0 END) DESC")
    Page<RestaurantEntity> findByRestaurantNameAndDongEntity_DistrictsEntity_CityEntity_CityNameAndLikeCountEntity_likeStatus(Pageable pageable, @Param("keyword") String keyword, String city);

    // 1. 별점 순 정렬 + 키워드
    @Query("SELECT r " +
            "FROM RestaurantEntity r " +
            "LEFT JOIN ReviewEntity review ON r.restaurantId = review.restaurantEntity.restaurantId " +
            "WHERE r.restaurantName LIKE %:keyword% " +
            "AND r.restaurantStatus = 'OPEN' " +
            "GROUP BY r.restaurantId " +
            "ORDER BY COALESCE(AVG(review.reviewStarRating),0) DESC")
    Page<RestaurantEntity> findByRate(Pageable pageable, @Param("keyword") String keyword);

    // 2. 별점 순 정렬 + 키워드, dong 입력
    @Query("SELECT r " +
            "FROM RestaurantEntity r " +
            "LEFT JOIN ReviewEntity review ON r.restaurantId = review.restaurantEntity.restaurantId " +
            "WHERE r.restaurantName LIKE %:keyword% " +
            "AND r.dongEntity.dongName =:dong " +
            "AND r.restaurantStatus = 'OPEN' " +
            "GROUP BY r.restaurantId " +
            "ORDER BY COALESCE(AVG(review.reviewStarRating),0) DESC ")
    Page<RestaurantEntity> findByRestaurantNameAndDongEntity_DongNameAndRate(Pageable pageable, @Param("keyword") String keyword,  String dong);

    // 3. 별점 순 정렬 + 키워드, district 입력
    @Query("SELECT r " +
            "FROM RestaurantEntity r " +
            "LEFT JOIN ReviewEntity review ON r.restaurantId = review.restaurantEntity.restaurantId " +
            "WHERE r.restaurantName LIKE %:keyword% " +
            "AND r.dongEntity.districtsEntity.districtsName =:district " +
            "AND r.restaurantStatus = 'OPEN' " +
            "GROUP BY r.restaurantId " +
            "ORDER BY COALESCE(AVG(review.reviewStarRating),0) DESC ")
    Page<RestaurantEntity> findByRestaurantNameAndDongEntity_DistrictsEntity_DistrictsNameAndRate(Pageable pageable, @Param("keyword") String keyword,  String district);

    // 4. 별점 순 정렬 + 키워드, city 입력
    @Query("SELECT r " +
            "FROM RestaurantEntity r " +
            "LEFT JOIN ReviewEntity review ON r.restaurantId = review.restaurantEntity.restaurantId " +
            "WHERE r.restaurantName LIKE %:keyword% " +
            "AND r.dongEntity.districtsEntity.cityEntity.cityName =:city " +
            "AND r.restaurantStatus = 'OPEN' " +
            "GROUP BY r.restaurantId " +
            "ORDER BY COALESCE(AVG(review.reviewStarRating),0) DESC ")
    Page<RestaurantEntity> findByRestaurantNameAndDongEntity_DistrictsEntity_CityEntity_CityNameAndRate(Pageable pageable, @Param("keyword") String keyword,  String city);

    // 별점 계산
    @Query("SELECT ROUND(COALESCE(AVG(review.reviewStarRating),0),2) " +
            "FROM RestaurantEntity r " +
            "LEFT JOIN ReviewEntity review ON r.restaurantId = review.restaurantEntity.restaurantId " +
            "WHERE r.restaurantId = :restaurantId")
    BigDecimal findStarRateByRestaurantId(Long restaurantId);





    // 지역 이름으로 검색
    @Query("SELECT r FROM RestaurantEntity r" +
            " WHERE r.dongEntity.dongName LIKE %:add%" +
            " OR r.dongEntity.districtsEntity.districtsName LIKE %:add%" +
            " OR r.dongEntity.districtsEntity.cityEntity.cityName LIKE %:add% " +
            "AND r.restaurantStatus = 'OPEN'")
    Page<RestaurantEntity> findByAddress(Pageable pageable, @Param("add") String add);

}
