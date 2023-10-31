package com.hamtaro.sunflowerplate.service;

import com.hamtaro.sunflowerplate.dto.AdminReportDto;
import com.hamtaro.sunflowerplate.dto.RequestUpdateDto;
import com.hamtaro.sunflowerplate.dto.admin.RestaurantMenuDto;
import com.hamtaro.sunflowerplate.dto.admin.RestaurantMenuUpdateDto;
import com.hamtaro.sunflowerplate.dto.admin.RestaurantSaveDto;
import com.hamtaro.sunflowerplate.dto.admin.UpdateRestaurantInfoDto;
import com.hamtaro.sunflowerplate.dto.restaurant.RestaurantDetailDto;
import com.hamtaro.sunflowerplate.dto.restaurant.RestaurantImageDto;
import com.hamtaro.sunflowerplate.dto.restaurant.RestaurantLikeCountDto;
import com.hamtaro.sunflowerplate.entity.address.DongEntity;
import com.hamtaro.sunflowerplate.entity.member.MemberEntity;
import com.hamtaro.sunflowerplate.entity.restaurant.RestaurantEntity;
import com.hamtaro.sunflowerplate.entity.restaurant.RestaurantImageEntity;
import com.hamtaro.sunflowerplate.entity.restaurant.RestaurantMenuEntity;
import com.hamtaro.sunflowerplate.entity.review.ReportEntity;
import com.hamtaro.sunflowerplate.entity.review.RequestEntity;
import com.hamtaro.sunflowerplate.entity.review.ReviewImageEntity;
import com.hamtaro.sunflowerplate.repository.ReportRepository;
import com.hamtaro.sunflowerplate.repository.RequestRepository;
import com.hamtaro.sunflowerplate.repository.ReviewImageRepository;
import com.hamtaro.sunflowerplate.repository.ReviewRepository;
import com.hamtaro.sunflowerplate.repository.member.MemberRepository;
import com.hamtaro.sunflowerplate.repository.restaurant.DongRepository;
import com.hamtaro.sunflowerplate.repository.restaurant.RestaurantImageRepository;
import com.hamtaro.sunflowerplate.repository.restaurant.RestaurantMenuRepository;
import com.hamtaro.sunflowerplate.repository.restaurant.RestaurantRepository;
import com.hamtaro.sunflowerplate.service.restaurant.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final RestaurantRepository restaurantRepository;
    private final DongRepository dongRepository;
    private final RestaurantMenuRepository restaurantMenuRepository;
    private final RestaurantImageRepository restaurantImageRepository;
    private final ImageService imageService;

    public ResponseEntity<?> saveRestaurant(RestaurantSaveDto restaurantSaveDto, List<MultipartFile> multipartFileList) throws IOException {

        // 동엔티티 설정
        DongEntity dong = dongRepository.findByDongName(restaurantSaveDto.getRestaurantAdministrativeDistrict().getDongName()).get();

        RestaurantEntity restaurantEntity = RestaurantEntity.builder()
                .restaurantName(restaurantSaveDto.getRestaurantName())
                .restaurantTelNum(restaurantSaveDto.getRestaurantTelNum())
                .restaurantAddress(restaurantSaveDto.getRestaurantAddress())
                .restaurantOpenTime(restaurantSaveDto.getRestaurantOpenTime())
                .restaurantBreakTime(restaurantSaveDto.getRestaurantBreakTime())
                .restaurantWebSite(restaurantSaveDto.getRestaurantWebSite())
                .restaurantStatus("OPEN")
                .dongEntity(dong)
                .build();

        Long restaurantId = restaurantRepository.save(restaurantEntity).getRestaurantId();

        List<RestaurantMenuDto> restaurantMenuDtoList = restaurantSaveDto.getRestaurantMenuDtoList();
        List<RestaurantMenuEntity> restaurantMenuEntityList = new ArrayList<>();

        for (RestaurantMenuDto restaurantMenuDto : restaurantMenuDtoList) {
            RestaurantMenuEntity restaurantMenuEntity = RestaurantMenuEntity
                    .builder()
                    .restaurantMenuName(restaurantMenuDto.getRestaurantMenuName())
                    .restaurantMenuPrice(restaurantMenuDto.getRestaurantMenuPrice())
                    .restaurantEntity(restaurantRepository.findByRestaurantId(restaurantId).get())
                    .build();
            restaurantMenuEntityList.add(restaurantMenuEntity);
        }

        restaurantMenuRepository.saveAll(restaurantMenuEntityList);

        if (multipartFileList != null) {
            String dirName = "restaurant" + restaurantId;
            imageService.upload(multipartFileList, dirName, restaurantEntity);
        }


        if (restaurantRepository.findById(restaurantId).isEmpty()) {
            return ResponseEntity.status(400).body("식당 등록에 실패하였습니다.");
        } else {
            return ResponseEntity.status(200).body("식당 등록에 성공하였습니다.");
        }

    }

    // 식당 정보 수정
    public ResponseEntity<?> updateRestaurantInfo(Long restaurantId, UpdateRestaurantInfoDto restaurantDto, List<MultipartFile> multipartFilelist) {
        Optional<RestaurantEntity> restaurantEntityOptional = restaurantRepository.findById(restaurantId);
        if (restaurantEntityOptional.isEmpty()) {
            return ResponseEntity.status(200).body("restaurantId가 존재하지 않습니다.");
        } else {
            RestaurantEntity restaurantEntity = restaurantEntityOptional.get();

            // 동엔티티 수정
            DongEntity dong = dongRepository.findByDongName(restaurantDto.getRestaurantAdministrativeDistrict().getDongName()).get();

            // 식당 정보 수정
            restaurantEntity.setRestaurantName(restaurantDto.getRestaurantName());
            restaurantEntity.setRestaurantTelNum(restaurantDto.getRestaurantTelNum());
            restaurantEntity.setRestaurantAddress(restaurantDto.getRestaurantAddress());
            restaurantEntity.setRestaurantOpenTime(restaurantDto.getRestaurantOpenTime());
            restaurantEntity.setRestaurantBreakTime(restaurantDto.getRestaurantBreakTime());
            restaurantEntity.setRestaurantWebSite(restaurantDto.getRestaurantWebSite());
            restaurantEntity.setRestaurantStatus(restaurantDto.getRestaurantStatus());
            restaurantEntity.setDongEntity(dong);

            // 기존 메뉴 엔티티 삭제
            List<RestaurantMenuEntity> existingMenuEntityList = restaurantEntity.getRestaurantMenuEntity();
            restaurantMenuRepository.deleteAll(existingMenuEntityList);//  기존 메뉴 엔티티 리스트 삭제
            restaurantEntity.getRestaurantMenuEntity().clear();

            // 수정할 메뉴 dto -> entity 변환
            List<RestaurantMenuEntity> restaurantMenuEntityList = new ArrayList<>();
            List<RestaurantMenuUpdateDto> restaurantMenuDtoList = restaurantDto.getRestaurantMenuDtoList();

            for (RestaurantMenuUpdateDto restaurantMenuUpdateDto : restaurantMenuDtoList) {
                RestaurantMenuEntity restaurantMenuEntity = RestaurantMenuEntity
                        .builder()
                        .restaurantMenuName(restaurantMenuUpdateDto.getRestaurantMenuName())
                        .restaurantMenuPrice(restaurantMenuUpdateDto.getRestaurantMenuPrice())
                        .restaurantEntity(restaurantEntity)
                        .build();
                restaurantMenuEntityList.add(restaurantMenuEntity);
            }

            // 수정 후 메뉴 엔티티 리스트 저장 후 레스토랑에 연결
            restaurantMenuRepository.saveAll(restaurantMenuEntityList);
            restaurantEntity.setRestaurantMenuEntity(restaurantMenuEntityList);

            // 대표 이미지 수정
            if (multipartFilelist != null) {
                List<RestaurantImageEntity> restaurantImageEntityList = restaurantImageRepository.findAllByRestaurantEntity_RestaurantId(restaurantId);
                for (RestaurantImageEntity restaurantImageEntity : restaurantImageEntityList) {
                    String originImagePath = restaurantImageEntity.getRestaurantOriginUrl();
                    String resizedImagePath = restaurantImageEntity.getRestaurantResizeUrl();

                    int originStartIndex = originImagePath.indexOf("restaurant" + restaurantId + "/");
                    String originFileName = originImagePath.substring(originStartIndex);
                    imageService.deleteS3File(originFileName);

                    int resizedStartIndex = resizedImagePath.indexOf("restaurant" + restaurantId + "/");
                    String resizedFileName = resizedImagePath.substring(resizedStartIndex);
                    imageService.deleteS3File(resizedFileName);
                }
                restaurantImageRepository.deleteAll(restaurantImageEntityList);

                String dirName = "restaurant" + restaurantId;
                imageService.upload(multipartFilelist, dirName, restaurantEntity);
            }

            // 변경된 내용을 저장
            restaurantRepository.save(restaurantEntity);

            return ResponseEntity.status(200).body("식당 정보 및 메뉴 엔티티가 업데이트되었습니다.");

        }
    }


    public ResponseEntity<?> deleteAdminReview(Long reviewId, String userId) {
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
