package com.hamtaro.sunflowerplate.entity.member;

import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Table(name = "member")
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;
    @Column(name = "member_email", length = 50, nullable = false, unique = true)
    private String memberEmail;
    @Column(name = "member_password", length = 15, nullable = false)
    private String memberPassword;
    @Column(name = "member_nickname", length = 20, nullable = false, unique = true)
    private String memberNickName;
    @Column(name = "member_number", length = 13, nullable = false, unique = true)
    private String memberNumber;
    @Column(name = "profile_image", length = 100)
    private String profileImage;
    //사용자 활성화 상태
    @Column(name = "status")
    private Boolean status = true;

    @Column(name = "member_role", length = 10, nullable = false)
    private String role;
}
