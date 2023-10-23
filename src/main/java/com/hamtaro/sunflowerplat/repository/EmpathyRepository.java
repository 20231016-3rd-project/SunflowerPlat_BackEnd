package com.hamtaro.sunflowerplat.repository;

import com.hamtaro.sunflowerplat.entity.member.MemberEntity;
import com.hamtaro.sunflowerplat.entity.review.EmpathyEntity;
import com.hamtaro.sunflowerplat.entity.review.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpathyRepository extends JpaRepository<EmpathyEntity , Long> {

       Optional<EmpathyEntity>findByMemberEntityAndReviewEntity(MemberEntity memberEntity , ReviewEntity reviewEntity);
}
