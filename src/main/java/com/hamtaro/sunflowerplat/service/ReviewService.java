package com.hamtaro.sunflowerplat.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hamtaro.sunflowerplat.dto.ReportDto;
import com.hamtaro.sunflowerplat.dto.RequestDto;
import com.hamtaro.sunflowerplat.dto.ReviewSaveDto;
import com.hamtaro.sunflowerplat.entity.member.MemberEntity;
import com.hamtaro.sunflowerplat.entity.restaurant.RestaurantEntity;
import com.hamtaro.sunflowerplat.entity.review.ReportEntity;
import com.hamtaro.sunflowerplat.entity.review.RequestEntity;
import com.hamtaro.sunflowerplat.entity.review.ReviewEntity;
import com.hamtaro.sunflowerplat.entity.review.ReviewImageEntity;
import com.hamtaro.sunflowerplat.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class ReviewService {


    private final ReviewRepository reviewRepository;

    private final RequestRepository requestRepository;

    private final ReviewImageRepository reviewImageRepository;

    private final RestaurantRepository restaurantRepository;

    private final MemberRepository memberRepository;

    private final ReportRepository reportRepository;
    //리뷰 삭제 맨 !
    public ResponseEntity<Map<String, String>> reviewDelete(Long reviewId) {
        Map<String, String> map = new HashMap<>();
        Optional<ReviewEntity> deleteById = reviewRepository.findById(reviewId);

        if (deleteById.isPresent()) {
            reviewRepository.deleteById(reviewId);
            map.put("message", "리뷰가 삭제되었습니다.");
            return ResponseEntity.status(200).body(map);
        } else {
            map.put("message", "이미 삭제 되었거나 존재하지 않는 리뷰입니다.");
            return ResponseEntity.status(404).body(map);
        }

    }

    public ResponseEntity<Map<String, String>> requestRestaurant(RequestDto requestDto, Long requestId) {
        Map<String, String> hash = new HashMap<>();
        Optional<RequestEntity> request = requestRepository.findById(requestId);
        //TODO : 관리자 ID 한테 수정 내용 전달


        if (request.isPresent()) {

            RequestEntity modify = RequestEntity.builder()
                    .requestId(requestDto.getRequestId())
                    .requestAt(requestDto.getRequestAt())
                    .requestContent(requestDto.getRequestContent())
                    .build();

            requestRepository.save(modify);
            hash.put("message", "식당 정보 및 폐업 신고 요청이 되었습니다.");
            return ResponseEntity.status(200).body(hash);
        } else {
            hash.put("message", "권한이 없습니다.");
            return ResponseEntity.status(403).body(hash);
        }
    }


    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }
    // file 형식이 잘못된 경우를 확인하기 위해 만들어진 로직이며, 파일 타입과 상관없이 업로드할 수 있게 하기 위해 .의 존재 유무만 판단하였습니다.
    private String getFileExtension(String filename) {
        try {
            return filename.substring(filename.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + filename + ")입니다.");
        }
    }

    public ResponseEntity<Map<String,String>> saveUserReview(ReviewSaveDto reviewSaveDto,List<MultipartFile> imageFile, Long userId){
        RestaurantEntity restaurantEntity = restaurantRepository.findByRestaurantId(reviewSaveDto.getRestaurantId()).get();
        MemberEntity memberEntity = (memberRepository.findById(userId)).get();
        ReviewEntity reviewSaveEntity = ReviewEntity.builder()
                .reviewContent(reviewSaveDto.getReviewContent())
                .reviewStarRating(reviewSaveDto.getReviewStarRating())
                .reviewAt(reviewSaveDto.getReviewAt())
                .memberEntity(memberEntity)
                .restaurantEntity(restaurantEntity)
                .build();
        ReviewEntity reviewEntity = reviewRepository.save(reviewSaveEntity);
        List<String> reviewImgFile = new ArrayList<>();
        for (MultipartFile multipartFile: imageFile){

            String fileName = multipartFile.getOriginalFilename();

            try{
                //이미지 객체 생성
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentType(multipartFile.getContentType());
                objectMetadata.setContentLength(multipartFile.getInputStream().available());
                String storedName = createFileName(fileName);
                amazonS3Client.putObject(new PutObjectRequest(bucketName,storedName,multipartFile.getInputStream(),objectMetadata));
                String accessUrl = amazonS3Client.getUrl(bucketName, storedName).toString();
                System.out.println(accessUrl);

                //이미지 저장
                reviewImageRepository.save(ReviewImageEntity.builder()
                        .reviewOriginName(fileName)
                        .reviewStoredName(storedName)
                        .reviewOriginUrl(accessUrl)
                        .reviewEntity(reviewEntity)
                        .build());

            } catch (IOException e){
                throw new RuntimeException();
            }
            reviewImgFile.add(fileName);
        }


        Map<String,String> map = new HashMap<>();
        Long result = reviewRepository.save(reviewSaveEntity).getReviewId();
        Optional<ReviewEntity> findId = reviewRepository.findByReviewId(result);
        if (findId.isPresent()){
            map.put("message", "리뷰가 등록되었습니다.");
            return ResponseEntity.status(200).body(map);
        }else {
            map.put("message","리뷰 등록에 실패하였습니다.");
            return ResponseEntity.status(200).body(map);
        }
    }
        public ResponseEntity<?> reportReview(ReportDto reportDto, Long useId){
        MemberEntity memberEntity = memberRepository.findById(useId).get();
        ReviewEntity reviewEntity = reviewRepository.findByReviewId(reportDto.getReviewId()).get();
        ReportEntity reportSaveEntity = ReportEntity.builder()
                .reportCategory(reportDto.getReportCategory())
                .reportContent(reportDto.getReportContent())
                .reportAt(reportDto.getReportAt())
                .reviewEntity(reviewEntity)
                .memberEntity(memberEntity)
                .build();
            Map<String, String> map = new HashMap<>();
            Long result = reportRepository.save(reportSaveEntity).getReportId();
            Optional<ReportEntity> findByReportId = reportRepository.findByReportId(result);

            if (findByReportId.isPresent()){
                map.put("message", "신고가 접수되었습니다.");
                return ResponseEntity.status(200).body(map);
            }else {
                map.put("message","신고가 접수되지 않았습니다.");
                return ResponseEntity.status(200).body(map);
            }

    }

}
