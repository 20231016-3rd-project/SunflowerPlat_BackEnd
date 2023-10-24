package com.hamtaro.sunflowerplate.entity.member;

import com.hamtaro.sunflowerplate.entity.review.*;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "member")
public class MemberEntity {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "member_email")
    private String memberEmail;

    @Column(name = "member_password")
    private String memberPassword;

    @Column(name = "member_nickname")
    private String memberNickname;

    @Column(name = "member_phone")
    private String memberPhone;

    @Column(name = "member_profile_picture")
    private String memberProfilePicture;

    @Column(name = "member_join_date")
    private LocalDate memberJoinDate;
    @Builder.Default
    @Column(name = "member_state")
    private Boolean memberState = true;

    @Column(name = "member_role")
    private String memberRole;

    @OneToMany(mappedBy = "memberEntity",cascade = CascadeType.REMOVE,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<ReportEntity> reportEntityList;

    @OneToMany(mappedBy = "memberEntity",cascade = CascadeType.REMOVE,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<ReviewEntity> reviewEntityList;

    @OneToMany(mappedBy = "memberEntity",cascade = CascadeType.REMOVE,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<RequestEntity> requestEntityList;

    @OneToMany(mappedBy = "memberEntity",cascade = CascadeType.REMOVE,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<EmpathyEntity> empathyEntityList;

    @OneToMany(mappedBy = "memberEntity",cascade = CascadeType.REMOVE,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<LikeCountEntity> likeCountEntityList;
}
