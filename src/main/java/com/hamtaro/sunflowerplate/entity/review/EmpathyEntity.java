package com.hamtaro.sunflowerplate.entity.review;


import com.hamtaro.sunflowerplate.entity.member.MemberEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "empathy")
public class EmpathyEntity {
//좋아요
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "empathy_id" )
    private Long empathyId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private ReviewEntity reviewEntity;

}
