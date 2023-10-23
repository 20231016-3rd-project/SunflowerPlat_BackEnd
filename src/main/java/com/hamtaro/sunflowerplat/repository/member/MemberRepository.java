package com.hamtaro.sunflowerplat.repository.member;

import com.hamtaro.sunflowerplat.entity.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    MemberEntity findByMemberEmail(String email);

    MemberEntity findByMemberNumber(String telNumber);

    MemberEntity findByMemberNickName(String nickName);

}
