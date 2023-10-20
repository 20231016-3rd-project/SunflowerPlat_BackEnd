package com.hamtaro.sunflowerplat.entity.review;


import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "review_image")
public class ReviewImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "review_origin_name")
    private String reviewOriginName;

    @Column(name = "review_stored_name")
    private String reviewStoredName;

    @Column(name = "review_origin_url")
    private String reviewOriginUrl;

    @Column(name = "review_resize_url")
    private String reviewResizeUrl;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private ReviewEntity reviewEntity;

}
