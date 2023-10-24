package com.hamtaro.sunflowerplate.service;

import com.hamtaro.sunflowerplate.dto.ReportDto;
import com.hamtaro.sunflowerplate.entity.review.ReportEntity;
import com.hamtaro.sunflowerplate.entity.review.ReviewEntity;
import com.hamtaro.sunflowerplate.repository.ReportRepository;
import com.hamtaro.sunflowerplate.repository.RequestRepository;
import com.hamtaro.sunflowerplate.repository.ReviewRepository;
import com.hamtaro.sunflowerplate.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class AdminService {
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ReportRepository reportRepository;
    private final RequestRepository requestRepository;

    public ResponseEntity<?> deleteAdminReview(Long reviewId){
        Map<String, String> result = new HashMap<>();
        Optional<ReviewEntity> deleteId = reviewRepository.findByReviewId(reviewId);
        if (deleteId.isPresent()){
            reviewRepository.deleteByReviewId(reviewId);
            result.put("message","리뷰가 삭제되었습니다.");
            return ResponseEntity.status(200).body(result);
        }else {
            result.put("message","권한이 없습니다.");
            return ResponseEntity.status(403).body(result);
        }

    }
    //관리자 신고내역 조회
    public ResponseEntity<?> adminReportCheck(ReportDto reportDto , String memberId) {


        List<ReportEntity> reportEntityList = reportRepository.findByMemberEntity_MemberId(Long.valueOf(memberId));

        List<ReportDto> reportDtoList = new ArrayList<>();
        for (ReportEntity reportEntity : reportEntityList) {
            ReportDto reportDtos = ReportDto.builder()
                    .reviewId(reportEntity.getReportId())
                    .reportCategory(reportEntity.getReportCategory())
                    .reportAt(reportEntity.getReportAt())
                    .memberId(reportEntity.getReportId())
                    .build();

            reportDtoList.add(reportDtos);
        }

        return ResponseEntity.status(200).body(reportDtoList);
    }
}
