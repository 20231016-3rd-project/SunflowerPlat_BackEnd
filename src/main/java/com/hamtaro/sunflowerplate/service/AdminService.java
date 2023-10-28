package com.hamtaro.sunflowerplate.service;

import com.hamtaro.sunflowerplate.dto.AdminReportDto;
import com.hamtaro.sunflowerplate.dto.RequestUpdateDto;
import com.hamtaro.sunflowerplate.entity.member.MemberEntity;
import com.hamtaro.sunflowerplate.entity.review.ReportEntity;
import com.hamtaro.sunflowerplate.entity.review.RequestEntity;
import com.hamtaro.sunflowerplate.entity.review.ReviewImageEntity;
import com.hamtaro.sunflowerplate.repository.ReportRepository;
import com.hamtaro.sunflowerplate.repository.RequestRepository;
import com.hamtaro.sunflowerplate.repository.ReviewImageRepository;
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
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewService reviewService;

    public ResponseEntity<?> deleteAdminReview(Long reviewId) {
        Map<String, String> result = new HashMap<>();
        Optional<List<ReviewImageEntity>> deleteId = reviewImageRepository.findReviewImageEntityByReviewEntity_ReviewId(reviewId);
        if (deleteId.isPresent()) {
            // 리뷰와 연결된 이미지를 삭제
            for (ReviewImageEntity imageEntity : deleteId.get()) {
                // S3에서 이미지를 삭제
                reviewService.deleteS3Image(imageEntity.getReviewStoredName());
                reviewService.deleteS3Image(imageEntity.getReviewResizeStoredName());
            }
            // 리뷰 삭제
            reviewRepository.deleteById(reviewId);
            result.put("message", "리뷰가 삭제되었습니다.");

            return ResponseEntity.status(200).body(result);
        } else {
            result.put("message", "권한이 없습니다.");
            return ResponseEntity.status(403).body(result);
        }

    }


    //관리자 신고내역 조회
    public ResponseEntity<?> adminReportCheck(String userId) {

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


    //식당 정보 수정 요청 확인 어떤값을 드려야 하는지 물어보기
    public ResponseEntity<?> adminRestaurantModifyCheck(String userId) {


        MemberEntity findByMemberId = memberRepository.findById(Long.valueOf(userId)).get();

        List<RequestEntity> byRequestId = requestRepository.findAll();

        List<RequestUpdateDto> requestUpdateDtoList = new ArrayList<>();

        for (RequestEntity requestEntity : byRequestId) {

            RequestUpdateDto requestUpdateDto = RequestUpdateDto.builder()
                    .requestId(requestEntity.getRequestId())
                    .requestAt(requestEntity.getRequestAt())
                    .memberId(findByMemberId.getMemberId())
                    .restaurantId(requestEntity.getRestaurantEntity().getRestaurantId())
                    .requestContent(requestEntity.getRequestContent())
                    .build();

            requestUpdateDtoList.add(requestUpdateDto);
        }
        return ResponseEntity.status(200).body(requestUpdateDtoList);
    }
}
