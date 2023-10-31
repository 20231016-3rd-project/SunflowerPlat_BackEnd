package com.hamtaro.sunflowerplate.entity.restaurant;

import com.hamtaro.sunflowerplate.entity.address.DongEntity;
import com.hamtaro.sunflowerplate.entity.review.LikeCountEntity;
import com.hamtaro.sunflowerplate.entity.review.RequestEntity;
import com.hamtaro.sunflowerplate.entity.review.ReviewEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "restaurant")
public class RestaurantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    private Long restaurantId;

    @Column(name = "restaurant_name", nullable = false, length = 20)
    private String restaurantName;

    @Column(name = "restaurant_tel_num", length = 13)
    private String restaurantTelNum;

    @Column(name = "restaurant_address", nullable = false, length = 20)
    private String restaurantAddress;

    @Column(name = "restaurant_open_time", nullable = false, length = 30)
    private String restaurantOpenTime;

    @Column(name = "restaurant_break_time", length = 30)
    private String restaurantBreakTime;

    @Column(name = "restaurant_web_site", length = 100)
    private String restaurantWebSite;

    @Column(name = "restaurant_status", length = 10)
    private String restaurantStatus;

    @OneToMany(mappedBy = "restaurantEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RestaurantImageEntity> restaurantImageEntity = new ArrayList<>();

    @OneToMany(mappedBy = "restaurantEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RestaurantMenuEntity> restaurantMenuEntity = new ArrayList<>();

    @OneToMany(mappedBy = "restaurantEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LikeCountEntity> likeCountEntityList;

    @OneToMany(mappedBy = "restaurantEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RequestEntity> requestEntityList;

    @OneToMany(mappedBy = "restaurantEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ReviewEntity> reviewEntityList;

    @ManyToOne
    @JoinColumn(name = "dong_id")
    private DongEntity dongEntity;
}
