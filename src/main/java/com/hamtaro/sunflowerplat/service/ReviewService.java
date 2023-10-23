package com.hamtaro.sunflowerplat.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hamtaro.sunflowerplat.dto.ReportDto;
import com.hamtaro.sunflowerplat.dto.ReviewDto;
import com.hamtaro.sunflowerplat.dto.ReviewSaveDto;
import com.hamtaro.sunflowerplat.entity.member.MemberEntity;
import com.hamtaro.sunflowerplat.entity.restaurant.RestaurantEntity;
import com.hamtaro.sunflowerplat.entity.review.ReportEntity;
import com.hamtaro.sunflowerplat.entity.review.ReviewEntity;
import com.hamtaro.sunflowerplat.entity.review.ReviewImageEntity;
import com.hamtaro.sunflowerplat.repository.MemberRepository;
import com.hamtaro.sunflowerplat.repository.RestaurantRepository;
import com.hamtaro.sunflowerplat.repository.ReviewImageRepository;
import com.hamtaro.sunflowerplat.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final RestaurantRepository restaurantRepository;
    private final MemberRepository memberRepository;
//    private final AmazonS3Client amazonS3Client;
//    @Value("${cloud.aws.s3.goods-bucket}")
//    private String bucketName;

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

    public ResponseEntity<Map<String,String>> saveUserReview(ReviewSaveDto reviewSaveDto, Long userId){
        RestaurantEntity restaurantEntity = restaurantRepository.findByRestaurantId(reviewSaveDto.getRestaurantId()).get();
        MemberEntity memberEntity = (memberRepository.findById(userId)).get();
        ReviewEntity reviewSaveEntity = ReviewEntity.builder()
                .reviewContent(reviewSaveDto.getReviewContent())
                .reviewStarRating(reviewSaveDto.getReviewStarRating())
                .reviewAt(reviewSaveDto.getReviewAt())
                .memberEntity(memberEntity)
                .restaurantEntity(restaurantEntity)
                .build();
//        ReviewEntity reviewEntity = reviewRepository.save(reviewSaveEntity);
//        List<String> reviewImgFile = new ArrayList<>();
//        for (MultipartFile multipartFile: imageFile){
//
//            String fileName = multipartFile.getOriginalFilename();
//
//            try{
//                //이미지 객체 생성
//                ObjectMetadata objectMetadata = new ObjectMetadata();
//                objectMetadata.setContentType(multipartFile.getContentType());
//                objectMetadata.setContentLength(multipartFile.getInputStream().available());
//                String storedName = createFileName(fileName);
//                amazonS3Client.putObject(new PutObjectRequest(bucketName,storedName,multipartFile.getInputStream(),objectMetadata));
//                String accessUrl = amazonS3Client.getUrl(bucketName, storedName).toString();
//                System.out.println(accessUrl);
//
//                //이미지 저장
//                reviewImageRepository.save(ReviewImageEntity.builder()
//                        .reviewOriginName(fileName)
//                        .reviewStoredName(storedName)
//                        .reviewOriginUrl(accessUrl)
//                        .build());
//
//
//            } catch (IOException e){
//                throw new RuntimeException();
//            }
//            reviewImgFile.add(fileName);
//        }
        Map<String,String> map = new HashMap<>();
        Long result = reviewRepository.save(reviewSaveEntity).getReviewId();
        Optional<ReviewEntity> findId = reviewRepository.findById(result);
        if (findId.isPresent()){
            map.put("message", "리뷰가 등록되었습니다.");
            return ResponseEntity.status(200).body(map);
        }else {
            map.put("message","리뷰 등록에 실패하였습니다.");
            return ResponseEntity.status(200).body(map);
        }
    }
//        public ResponseEntity<?> reportReview(ReportDto reportDto, Long useId){
//        RestaurantEntity restaurantEntity = restaurantRepository.findByRestaurantId(reportDto.getRestaurantId()).get();
//        MemberEntity memberEntity = memberRepository.findById(useId).get();
//        ReviewEntity reviewEntity = reviewRepository.findById(reportDto.getReviewId()).get();
//        ReportEntity reportEntity = ReportEntity.builder()
//                .reportCategory(reportDto.getReportCategory())
//                .reportContent(reportDto.getReportContent())
//                .reportAt(reportDto.getReportAt())
//                .reviewEntity(reviewEntity)
//                .memberEntity(memberEntity)
//                .build();
//    }

}
