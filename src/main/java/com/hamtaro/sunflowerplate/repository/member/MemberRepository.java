package com.hamtaro.sunflowerplate.repository.member;

import com.hamtaro.sunflowerplate.entity.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    MemberEntity findByMemberEmail(String email);

    MemberEntity findByMemberPhone(String telNumber);

    MemberEntity findByMemberNickname(String nickName);

}
