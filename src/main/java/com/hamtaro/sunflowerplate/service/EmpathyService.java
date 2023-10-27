package com.hamtaro.sunflowerplate.service;


import com.amazonaws.services.kms.model.NotFoundException;
import com.hamtaro.sunflowerplate.dto.EmpathyDto;
import com.hamtaro.sunflowerplate.entity.member.MemberEntity;
import com.hamtaro.sunflowerplate.entity.review.EmpathyEntity;
import com.hamtaro.sunflowerplate.entity.review.ReviewEntity;
import com.hamtaro.sunflowerplate.repository.EmpathyRepository;
import com.hamtaro.sunflowerplate.repository.ReviewRepository;
import com.hamtaro.sunflowerplate.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Log4j2
@Service
@RequiredArgsConstructor
public class EmpathyService {

    private final EmpathyRepository empathyRepository;
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;


    @Transactional
    public EmpathyDto countPlus(Long reviewId, String userId) {



        MemberEntity memberEntity = memberRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new NotFoundException("Could not found user id : "
                        + memberRepository.findById(Long.valueOf(userId))));

        ReviewEntity reviewEntity = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Could not found review id : "
                        + reviewRepository.findById(reviewId)));

        EmpathyDto empathyResponse = new EmpathyDto();

        // memberEntity가 reviewEntity를 이미 좋아요한 경우 좋아요를 다시 누르면 좋아요가 취소.
        if (memberEntity.getEmpathyEntityList().stream()
                .anyMatch(empathyEntity -> empathyEntity.getMemberEntity().equals(memberEntity))){


            empathyRepository.deleteByMemberEntityAndReviewEntity(memberEntity, reviewEntity);

            empathyRepository.countByReviewEntity(reviewEntity);
            empathyResponse.setMessage("좋아요 취소");

        } else {
            empathyRepository.save(EmpathyEntity.builder()
                    .reviewEntity(reviewEntity)
                    .memberEntity(memberEntity)
                    .build());

            empathyRepository.countByReviewEntity(reviewEntity);
            empathyResponse.setMessage("좋아요 성공");


        }
        return empathyResponse;
    }
}


