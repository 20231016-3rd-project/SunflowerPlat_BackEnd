package com.hamtaro.sunflowerplate.service.restaurant;

import com.hamtaro.sunflowerplate.dto.admin.RestaurantMenuDto;
import com.hamtaro.sunflowerplate.dto.restaurant.RestaurantDetailDto;
import com.hamtaro.sunflowerplate.dto.restaurant.RestaurantDto;
import com.hamtaro.sunflowerplate.dto.restaurant.RestaurantImageDto;
import com.hamtaro.sunflowerplate.dto.restaurant.RestaurantLikeCountDto;
import com.hamtaro.sunflowerplate.entity.restaurant.RestaurantEntity;
import com.hamtaro.sunflowerplate.entity.restaurant.RestaurantImageEntity;
import com.hamtaro.sunflowerplate.entity.restaurant.RestaurantMenuEntity;
import com.hamtaro.sunflowerplate.repository.restaurant.*;
import com.hamtaro.sunflowerplate.repository.member.MemberRepository;
import com.hamtaro.sunflowerplate.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final DongRepository dongRepository;
    private final RestaurantMenuRepository restaurantMenuRepository;
    private final RestaurantImageRepository restaurantImageRepository;
    private final LikeCountRepository likeCountRepository;
    private final MemberRepository memberRepository;
    private final ImageService imageService;
    private final ReviewService reviewService;

    // 식당 정보 조회 - 리뷰 조회 추가 필요
    public ResponseEntity<?> findRestaurantDetailsById(Long restaurantId, int reviewPage,String reviewSort, String userId) {
        Optional<RestaurantEntity> restaurantEntityOptional = restaurantRepository.findById(restaurantId);

        if (restaurantEntityOptional.isEmpty()) {
            return ResponseEntity.status(200).body("식당이 존재하지 않습니다.");
        } else {
            RestaurantEntity restaurantEntity = restaurantEntityOptional.get();

            // 메뉴 리스트 불러오기
            List<RestaurantMenuDto> restaurantMenuDtoList = new ArrayList<>();
            for (RestaurantMenuEntity restaurantMenuEntity : restaurantEntity.getRestaurantMenuEntity()) {
                RestaurantMenuDto restaurantMenuDto = RestaurantMenuDto
                        .builder()
                        .restaurantMenuName(restaurantMenuEntity.getRestaurantMenuName())
                        .restaurantMenuPrice(restaurantMenuEntity.getRestaurantMenuPrice())
                        .build();
                restaurantMenuDtoList.add(restaurantMenuDto);
            }

            // 이미지 불러오기
            List<RestaurantImageDto> restaurantImageDtoList = new ArrayList<>();
            for(RestaurantImageEntity restaurantImageEntity : restaurantEntity.getRestaurantImageEntity()){
                RestaurantImageDto restaurantImageDto = RestaurantImageDto
                        .builder()
                        .restaurantOriginName(restaurantImageEntity.getRestaurantOriginName())
                        .restaurantStoredName(restaurantImageEntity.getRestaurantStoredName())
                        .restaurantOriginUrl(restaurantImageEntity.getRestaurantOriginUrl())
                        .restaurantResizedStoredName(restaurantImageEntity.getRestaurantResizedStoredName())
                        .restaurantResizeUrl(restaurantImageEntity.getRestaurantResizeUrl())
                        .build();
                restaurantImageDtoList.add(restaurantImageDto);
            }

            boolean likeButton;
            if(userId.equals("notLogin")) {
                likeButton = false;
            } else {
                Long memberId = Long.valueOf(userId);
                likeButton = likeCountRepository.findByMemberEntityAndRestaurantEntity(memberId, restaurantId).isPresent();
            }

            // 좋아요 불러오기
            RestaurantLikeCountDto restaurantLikeCountDto = RestaurantLikeCountDto
                    .builder()
                    .restaurantLikeCount(likeCountRepository.countByRestaurantEntity_RestaurantId(restaurantId))
                    .likedRestaurant(likeButton)
                    .build();

            // Entity -> Dto 변환
            RestaurantDetailDto restaurantDetailDto = RestaurantDetailDto
                    .builder()
                    .restaurantName(restaurantEntity.getRestaurantName())
                    .restaurantStarRate(restaurantRepository.findStarRateByRestaurantId(restaurantId))
                    .restaurantStatus(restaurantEntity.getRestaurantStatus())
                    .restaurantTelNum(restaurantEntity.getRestaurantTelNum())
                    .restaurantAddress(restaurantEntity.getRestaurantAddress())
                    .restaurantOpenTime(restaurantEntity.getRestaurantOpenTime())
                    .restaurantWebSite(restaurantEntity.getRestaurantWebSite())
                    .restaurantLikeCountDto(restaurantLikeCountDto)
                    .restaurantImageDtoList(restaurantImageDtoList)
                    .restaurantMenuDtoList(restaurantMenuDtoList)
                    .reviewReturnDtoPage(reviewService.findReviewPageByRestaurant(restaurantId,reviewPage,reviewSort, userId))
                    .build();

            return ResponseEntity.status(200).body(restaurantDetailDto);
        }
    }

    // 식당 검색 - 리뷰 많은순, 별점 순 정렬 필요, 좋아요 순 완료
    public ResponseEntity<?> findRestaurantByKeyword (int page, String sort, String keyword, String city, String district, String dong) {
        Pageable pageable;
        Page<RestaurantDto> restaurantDtoPage;
        Page<RestaurantEntity> restaurantEntityPage;

        if(sort.equals("rateDesc")) { // 별점 순 정렬
            pageable = PageRequest.of(page,10);
            if(dong != null){ // 동 이름 + 키워드 검색
                restaurantEntityPage = restaurantRepository.findByRestaurantNameAndDongEntity_DongNameAndRate(pageable,keyword,dong);
            } else if (district != null) { // 구 이름 + 키워드 검색
                restaurantEntityPage = restaurantRepository.findByRestaurantNameAndDongEntity_DistrictsEntity_DistrictsNameAndRate(pageable,keyword,district);
            } else if (city != null) { // 시 이름 + 키워드 검색
                restaurantEntityPage = restaurantRepository.findByRestaurantNameAndDongEntity_DistrictsEntity_CityEntity_CityNameAndRate(pageable, keyword, city);
            } else { // 키워드 검색
                restaurantEntityPage = restaurantRepository.findByRate(pageable,keyword);
            }
        } else if (sort.equals("like")) { // 좋아요 순 정렬
            pageable = PageRequest.of(page,10);
            if(dong != null){ // 동 이름 + 키워드 검색
                restaurantEntityPage = restaurantRepository.findByRestaurantNameAndDongEntity_DongNameAndLikeCountEntity_likeStatus(pageable,keyword,dong);
            } else if (district != null) { // 구 이름 + 키워드 검색
                restaurantEntityPage = restaurantRepository.findByRestaurantNameAndDongEntity_DistrictsEntity_DistrictsNameAndLikeCountEntity_likeStatus(pageable,keyword,district);
            } else if (city != null) { // 시 이름 + 키워드 검색
                restaurantEntityPage = restaurantRepository.findByRestaurantNameAndDongEntity_DistrictsEntity_CityEntity_CityNameAndLikeCountEntity_likeStatus(pageable, keyword, city);
            } else { // 키워드 검색
                restaurantEntityPage = restaurantRepository.findByRestaurantNameAndLikeCountEntity_likeStatus(pageable,keyword);
            }
        } else if(sort.equals("review")) {
            pageable = PageRequest.of(page,10, Sort.by(Sort.Direction.DESC, "reviewEntityList.size"));

            if(dong != null){ // 동 이름 + 키워드 검색
                restaurantEntityPage = restaurantRepository.findByRestaurantNameAndDongEntity_DongName(pageable,keyword,dong);
            } else if (district != null) { // 구 이름 + 키워드 검색
                restaurantEntityPage = restaurantRepository.findByRestaurantNameAndDongEntity_DistrictsEntity_DistrictsName(pageable,keyword,district);
            } else if (city != null) { // 시 이름 + 키워드 검색
                restaurantEntityPage = restaurantRepository.findByRestaurantNameAndDongEntity_DistrictsEntity_CityEntity_CityName(pageable, keyword, city);
            } else { // 키워드 검색
                restaurantEntityPage = restaurantRepository.findByRestaurantName(pageable,keyword);
            }
        } else {
            return ResponseEntity.status(400).body("잘못된 접근입니다.\n존재 하지 않는 sort");
        }

        restaurantDtoPage = restaurantEntityPage
                .map(this::restaurantEntityToRestaurantDto);
        return ResponseEntity.status(200).body(restaurantDtoPage);
    }

    // Entity -> Dto
    private RestaurantDto restaurantEntityToRestaurantDto(RestaurantEntity restaurantEntity){
        return RestaurantDto
                .builder()
                .restaurantId(restaurantEntity.getRestaurantId())
                .restaurantName(restaurantEntity.getRestaurantName())
                .restaurantStatus(restaurantEntity.getRestaurantStatus())
                .restaurantAddress(restaurantEntity.getRestaurantAddress())
                .restaurantWebSite(restaurantEntity.getRestaurantWebSite())
                .resizedImageUrl(restaurantEntity.getRestaurantImageEntity()
                        .stream()
                        .findFirst()
                        .map(RestaurantImageEntity::getRestaurantResizeUrl)
                        .orElse("null"))
                .likeCount(likeCountRepository.countByRestaurantEntity_RestaurantId(restaurantEntity.getRestaurantId()))
                .reviewCount(restaurantEntity.getReviewEntityList().size())
                .avgStarRate(restaurantRepository.findStarRateByRestaurantId(restaurantEntity.getRestaurantId()))
                .build();
    }

}