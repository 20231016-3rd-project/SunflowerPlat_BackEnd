package com.hamtaro.sunflowerplate.service.member;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hamtaro.sunflowerplate.dto.ReviewDto;
import com.hamtaro.sunflowerplate.dto.ReviewImageDto;
import com.hamtaro.sunflowerplate.dto.member.RequestMyPlaceDto;
import com.hamtaro.sunflowerplate.dto.member.RequestMyReviewDto;
import com.hamtaro.sunflowerplate.dto.member.UpdateReviewDto;
import com.hamtaro.sunflowerplate.entity.member.MemberEntity;
import com.hamtaro.sunflowerplate.entity.restaurant.RestaurantImageEntity;
import com.hamtaro.sunflowerplate.entity.review.LikeCountEntity;
import com.hamtaro.sunflowerplate.entity.review.ReviewEntity;
import com.hamtaro.sunflowerplate.entity.review.ReviewImageEntity;
import com.hamtaro.sunflowerplate.repository.LikeCountRepository;
import com.hamtaro.sunflowerplate.repository.ReviewImageRepository;
import com.hamtaro.sunflowerplate.repository.ReviewRepository;
import com.hamtaro.sunflowerplate.repository.member.MemberRepository;
import com.hamtaro.sunflowerplate.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewService reviewService;
    private final LikeCountRepository likeCountRepository;
    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.review-img}")
    private String bucketName;

    public ResponseEntity<?> getMyReview(String userId) {
        MemberEntity findId = memberRepository.findById(Long.valueOf(userId)).get();
        List<ReviewEntity> myReview = reviewRepository.findByMemberEntity_MemberId(findId.getMemberId());
        List<RequestMyReviewDto> requestMyReviewDtoList = new ArrayList<>();
        for (ReviewEntity reviewEntity : myReview) {
            List<ReviewImageDto> reviewImageDtoList = new ArrayList<>();

            for (ReviewImageEntity reviewImageEntity : reviewEntity.getReviewImageEntityList()) {
                ReviewImageDto reviewImageDto =
                        ReviewImageDto.builder()
                                      .reviewImageId(reviewImageEntity.getReviewImageId())
                                      .reviewOriginName(reviewImageEntity.getReviewOriginName())
                                      .reviewOriginUrl(reviewImageEntity.getReviewOriginUrl())
                                      .reviewResizeUrl(reviewImageEntity.getReviewResizeUrl())
                                      .reviewStoredName(reviewImageEntity.getReviewStoredName())
                                      .build();
                reviewImageDtoList.add(reviewImageDto);
            }
            RequestMyReviewDto requestMyReviewDto =
                    RequestMyReviewDto.builder()
                                      .restaurantId(reviewEntity.getRestaurantEntity().getRestaurantId()) // 레스토랑ID
                                      .restaurantName(reviewEntity.getRestaurantEntity().getRestaurantName()) //레스토랑이름
                                      .reviewContent(reviewEntity.getReviewContent()) //리뷰내용
                                      .reviewStarRating(reviewEntity.getReviewStarRating()) //리뷰별점
                                      .reviewImageDto(reviewImageDtoList)
                                      .reviewAt(reviewEntity.getReviewAt()) // 리뷰작성시간
                                      .build();
            requestMyReviewDtoList.add(requestMyReviewDto);
        }
        return ResponseEntity.status(200).body(requestMyReviewDtoList);
    }

    public ResponseEntity<?> deleteMyReview(Long reviewId) {
        Optional<List<ReviewImageEntity>> reviewImageId = reviewImageRepository.findReviewImageEntityByReviewEntity_ReviewId(reviewId);
        if (reviewImageId.isPresent()) {
            for (ReviewImageEntity reviewImageEntity : reviewImageId.get()) {
                reviewService.deleteS3Image(reviewImageEntity.getReviewStoredName());
                reviewService.deleteS3Image(reviewImageEntity.getReviewResizeStoredName());
            }
            reviewRepository.deleteById(reviewId);
            return ResponseEntity.status(200).body("리뷰삭제");
        } else {
            reviewRepository.deleteById(reviewId);
            return ResponseEntity.status(200).body("리뷰삭제");
        }
    }
    private File resizeImage(MultipartFile originalImage) throws IOException {
        File resizeFile = new File("resized_" + originalImage.getOriginalFilename());
        Thumbnails.of(originalImage.getInputStream())
                  .size(400, 400)
                  .toFile(resizeFile);
        return resizeFile;
    }
    private String getFileExtension(String filename) {
        try {
            return filename.substring(filename.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + filename + ")입니다.");
        }
    }
    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    // 업데이트 아직 진행중
    public ResponseEntity<?> updateMyReview(Long reviewId, UpdateReviewDto updateReviewDto) {
        ReviewEntity findReview = reviewRepository.findById(reviewId).get();

        for (ReviewImageEntity reviewImageEntity : findReview.getReviewImageEntityList()) {
            reviewService.deleteS3Image(reviewImageEntity.getReviewStoredName());
            reviewService.deleteS3Image(reviewImageEntity.getReviewResizeStoredName());
        }
        findReview.setReviewContent(updateReviewDto.getReviewContent());
        Long saveReviewId = reviewRepository.save(findReview).getReviewId();
        ReviewEntity updateReview = reviewRepository.findById(saveReviewId).get();

        for (MultipartFile multipartFile: updateReviewDto.getImageFile()){

            String fileName = multipartFile.getOriginalFilename(); //원본 파일

            try {
                //이미지 객체 생성
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentType(multipartFile.getContentType());
                objectMetadata.setContentLength(multipartFile.getSize());

                //원본 파일 저장
                String storedName = createFileName(fileName);
                amazonS3Client.putObject(new PutObjectRequest(bucketName,storedName,multipartFile.getInputStream(),objectMetadata));
                String accessUrl = amazonS3Client.getUrl(bucketName, storedName).toString();
                System.out.println("Original Image URL:" + accessUrl);

                //리사이징 파일 저장
                String resizeName = "resized_" + storedName;
                File resizedImageFile = resizeImage(multipartFile);
                amazonS3Client.putObject(bucketName,resizeName,resizedImageFile);
                String resizeUrl = amazonS3Client.getUrl(bucketName,resizeName).toString();

                //이미지 저장
                reviewImageRepository.save(ReviewImageEntity.builder()
                                                            .reviewOriginName(fileName)
                                                            .reviewStoredName(storedName)
                                                            .reviewResizeStoredName(resizeName)
                                                            .reviewOriginUrl(accessUrl)
                                                            .reviewResizeUrl(resizeUrl)
                                                            .reviewEntity(updateReview)
                                                            .build());

                if (resizedImageFile != null && resizedImageFile.exists()) {
                    if (resizedImageFile.delete()) {
                        log.info("이미지 삭제됨");
                    } else {
                        log.info("이미지 삭제");
                    }
                } else {
                    log.info("이미지파일 없음");
                }
            } catch (IOException e){
                throw new RuntimeException("이미지 업로드에 실패했습니다.");
            }
        }
        return null;
    }

    public ResponseEntity<?> getMyPlace(String userId) {
        MemberEntity findId = memberRepository.findById(Long.valueOf(userId)).get();
        List<LikeCountEntity> myPlace = likeCountRepository.findByMemberEntity_MemberId(findId.getMemberId());
        List<RequestMyPlaceDto> requestMyPlaceDtoList = new ArrayList<>();
        for (LikeCountEntity likeCountEntity : myPlace) {
            RequestMyPlaceDto requestMyPlaceDto = RequestMyPlaceDto.builder()
                                                                   .restaurantId(likeCountEntity.getRestaurantEntity()
                                                                                                .getRestaurantId())
                                                                   .restaurantName(likeCountEntity.getRestaurantEntity()
                                                                                                  .getRestaurantName())
                                                                   .restaurantAddress(likeCountEntity.getRestaurantEntity()
                                                                                                     .getRestaurantAddress())
                                                                   .restaurantWebSite(likeCountEntity.getRestaurantEntity()
                                                                                                     .getRestaurantWebSite())
                                                                   .resizeImgUrl(likeCountEntity.getRestaurantEntity()
                                                                                                .getRestaurantImageEntity()
                                                                                                .get(0)
                                                                                                .getRestaurantResizeUrl())
                                                                   .build();
            requestMyPlaceDtoList.add(requestMyPlaceDto);
        }
        return ResponseEntity.status(200).body(requestMyPlaceDtoList);
    }
}
