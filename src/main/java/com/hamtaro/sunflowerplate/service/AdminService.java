package com.hamtaro.sunflowerplate.service;

import com.hamtaro.sunflowerplate.dto.AdminReportDto;
import com.hamtaro.sunflowerplate.dto.RequestUpdateDto;
import com.hamtaro.sunflowerplate.entity.review.ReportEntity;
import com.hamtaro.sunflowerplate.entity.review.RequestEntity;
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

    public ResponseEntity<?> deleteAdminReview(Long reviewId) {
        Map<String, String> result = new HashMap<>();
        Optional<ReviewEntity> deleteId = reviewRepository.findByReviewId(reviewId);
        if (deleteId.isPresent()) {
            reviewRepository.deleteById(reviewId);
            result.put("message", "리뷰가 삭제되었습니다.");
            return ResponseEntity.status(200).body(result);
        } else {
            result.put("message", "권한이 없습니다.");
            return ResponseEntity.status(403).body(result);
        }

    }


    //관리자 신고내역 조회
    public ResponseEntity<?> adminReportCheck() {

        List<ReportEntity> reportEntityList = reportRepository.findAll();

        List<AdminReportDto> reportDtoList = new ArrayList<>();

        for (ReportEntity reportEntity : reportEntityList) {

            AdminReportDto reportDos = AdminReportDto.builder()
                    .reviewId(reportEntity.getReportId())
                    .reviewAuthor(reportEntity.getReviewEntity().getMemberEntity().getMemberNickname())
                    .reviewProfilePicture(reportEntity.getReviewEntity().getMemberEntity().getMemberProfilePicture())
                    .reportContent(reportEntity.getReportContent())
                    .reportCategory(reportEntity.getReportCategory())
                    .reportAt(reportEntity.getReportAt())
                    .nickName(reportEntity.getMemberEntity().getMemberNickname())
                    .memberId(reportEntity.getMemberEntity().getMemberId())
                    .memberProfilePicture(reportEntity.getMemberEntity().getMemberProfilePicture())
                    .build();

            reportDtoList.add(reportDos);
        }

        return ResponseEntity.status(200).body(reportDtoList);
    }


    //식당 정보 수정 요청 확인 TODO : 어떤값을 드려야 하는지 물어보기
    public ResponseEntity<?> adminRestaurantModifyCheck() {

        List<RequestEntity> byRequestId = requestRepository.findAll();

        List<RequestUpdateDto> requestUpdateDtoList = new ArrayList<>();

        for (RequestEntity requestEntity : byRequestId) {

            RequestUpdateDto requestUpdateDto = RequestUpdateDto.builder()
                    .requestId(requestEntity.getRequestId())
                    .requestAt(requestEntity.getRequestAt())
                    .memberId(requestEntity.getMemberEntity().getMemberId())
                    .restaurantId(requestEntity.getRestaurantEntity().getRestaurantId())
                    .requestContent(requestEntity.getRequestContent())
                    .build();

            requestUpdateDtoList.add(requestUpdateDto);
        }
        return ResponseEntity.status(200).body(requestUpdateDtoList);
    }
}
