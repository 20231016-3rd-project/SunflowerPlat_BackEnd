package com.hamtaro.sunflowerplate.repository;

import com.hamtaro.sunflowerplate.entity.member.MemberEntity;
import com.hamtaro.sunflowerplate.entity.review.EmpathyEntity;
import com.hamtaro.sunflowerplate.entity.review.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpathyRepository extends JpaRepository<EmpathyEntity , Long> {



       void deleteByMemberEntityAndReviewEntity(MemberEntity memberEntity , ReviewEntity reviewEntity);

       long countByReviewEntity(ReviewEntity reviewEntity);

}
