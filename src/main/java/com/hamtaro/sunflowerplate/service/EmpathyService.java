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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;


@Log4j2
@Service
@RequiredArgsConstructor
public class EmpathyService {

    private final EmpathyRepository empathyRepository;
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public ResponseEntity<Map<String, String>> countPlus(EmpathyDto empathyDto,String userId) throws Exception {


        MemberEntity memberEntity = memberRepository.findById(empathyDto.getMemberId())
                .orElseThrow(() -> new NotFoundException("Could not found member id : " + empathyDto.getMemberId()));

        ReviewEntity reviewEntity = reviewRepository.findById(empathyDto.getReviewId())
                .orElseThrow(() -> new NotFoundException("Could not found review id : " + empathyDto.getReviewId()));

        Map<String, String> map = new HashMap<>();

        //memberEntity, reviewEntity 존재 할경우 다시 누르면 좋아요 취소
        if (empathyRepository.findByMemberEntityAndReviewEntity(memberEntity, reviewEntity).isPresent()) {


            EmpathyEntity empathyEntities = empathyRepository.findById(empathyDto.getEmpathyId())
                    .orElseThrow(() -> new NotFoundException("Could not found empathy id : "));

            empathyRepository.delete(empathyEntities);

            map.put("message", "좋아요 취소 되었습니다~");
            return ResponseEntity.status(200).body(map);

        } else {
            EmpathyEntity count = EmpathyEntity.builder()
                    .memberEntity(memberEntity)
                    .reviewEntity(reviewEntity)
                    .build();
            empathyRepository.save(count);
        }
        map.put("message", "좋아요~");
        return ResponseEntity.status(200).body(map);
    }

}


