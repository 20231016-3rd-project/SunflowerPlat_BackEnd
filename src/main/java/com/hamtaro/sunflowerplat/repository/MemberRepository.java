package com.hamtaro.sunflowerplat.repository;

import com.hamtaro.sunflowerplat.entity.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity,Long> {
}
