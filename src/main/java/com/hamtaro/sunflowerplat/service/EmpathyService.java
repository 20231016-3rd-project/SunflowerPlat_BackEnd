package com.hamtaro.sunflowerplat.service;


import com.amazonaws.services.kms.model.NotFoundException;
import com.hamtaro.sunflowerplat.dto.EmpathyDto;
import com.hamtaro.sunflowerplat.entity.member.MemberEntity;
import com.hamtaro.sunflowerplat.entity.review.EmpathyEntity;
import com.hamtaro.sunflowerplat.entity.review.ReviewEntity;
import com.hamtaro.sunflowerplat.repository.EmpathyRepository;
import com.hamtaro.sunflowerplat.repository.MemberRepository;
import com.hamtaro.sunflowerplat.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class EmpathyService {

    private final EmpathyRepository empathyRepository;
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public ResponseEntity<Map<String,String>> countPlus(EmpathyDto empathyDto) throws Exception {


        MemberEntity memberEntity = memberRepository.findById(empathyDto.getMemberId())
                .orElseThrow(() -> new NotFoundException("Could not found member id : " + empathyDto.getMemberId()));

        ReviewEntity reviewEntity = reviewRepository.findById(empathyDto.getReviewId())
                .orElseThrow(() -> new NotFoundException("Could not found review id : " + empathyDto.getReviewId()));
        Map<String, String> map = new HashMap<>();

        EmpathyEntity count = EmpathyEntity.builder()
                .memberEntity(memberEntity)
                .reviewEntity(reviewEntity)
                .build();

        empathyRepository.save(count);

        //이미 좋아요 있으면 에러 반환
        if (empathyRepository.findByMemberAndReview(memberEntity, reviewEntity).isPresent()) {
            //409 에러 딱
            map.put("message", "이미 좋아요를 누르셨습니다.");
            return ResponseEntity.status(409).body(map);




        }else {
             map.put("message","좋아요 눌러졌습니다.");
             return  ResponseEntity.status(200).body(map);
        }
    }
}
