package com.hamtaro.sunflowerplate.service.restaurant;

import com.hamtaro.sunflowerplate.dto.admin.RestaurantMenuUpdateDto;
import com.hamtaro.sunflowerplate.dto.admin.RestaurantSaveDto;
import com.hamtaro.sunflowerplate.dto.admin.RestaurantMenuDto;
import com.hamtaro.sunflowerplate.dto.admin.UpdateRestaurantInfoDto;
import com.hamtaro.sunflowerplate.dto.restaurant.RestaurantDetailDto;
import com.hamtaro.sunflowerplate.dto.restaurant.RestaurantDto;
import com.hamtaro.sunflowerplate.dto.restaurant.RestaurantImageDto;
import com.hamtaro.sunflowerplate.dto.restaurant.RestaurantLikeCountDto;
import com.hamtaro.sunflowerplate.entity.address.DongEntity;
import com.hamtaro.sunflowerplate.entity.restaurant.RestaurantEntity;
import com.hamtaro.sunflowerplate.entity.restaurant.RestaurantImageEntity;
import com.hamtaro.sunflowerplate.entity.restaurant.RestaurantMenuEntity;
import com.hamtaro.sunflowerplate.repository.restaurant.*;
import com.hamtaro.sunflowerplate.repository.member.MemberRepository;
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

    // 식당 정보 조회 - 리뷰 조회 추가 필요
    public ResponseEntity<?> findRestaurantDetailsById(Long restaurantId) {
        Optional<RestaurantEntity> restaurantEntityOptional = restaurantRepository.findById(restaurantId);

        if (restaurantEntityOptional.isEmpty()) {
            return ResponseEntity.status(200).body("식당이 존재하지 않습니다.");
        } else {
            RestaurantEntity restaurantEntity = restaurantEntityOptional.get();

            List<RestaurantMenuDto> restaurantMenuDtoList = new ArrayList<>();
            for (RestaurantMenuEntity restaurantMenuEntity : restaurantEntity.getRestaurantMenuEntity()) {
                RestaurantMenuDto restaurantMenuDto = RestaurantMenuDto
                        .builder()
                        .restaurantMenuName(restaurantMenuEntity.getRestaurantMenuName())
                        .restaurantMenuPrice(restaurantMenuEntity.getRestaurantMenuPrice())
                        .build();
                restaurantMenuDtoList.add(restaurantMenuDto);
            }

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

            RestaurantLikeCountDto restaurantLikeCountDto = RestaurantLikeCountDto
                    .builder()
                    .restaurantLikeCount(likeCountRepository.countByRestaurantEntity_RestaurantId(restaurantId))
                    .likedRestaurant(false) // 로그인 후 좋아요 여부 체크 기능 필요
                    .build();


            RestaurantDetailDto restaurantDetailDto = RestaurantDetailDto
                    .builder()
                    .restaurantName(restaurantEntity.getRestaurantName())
                    .restaurantTelNum(restaurantEntity.getRestaurantTelNum())
                    .restaurantAddress(restaurantEntity.getRestaurantAddress())
                    .restaurantOpenTime(restaurantEntity.getRestaurantOpenTime())
                    .restaurantBreakTime(restaurantEntity.getRestaurantBreakTime())
                    .restaurantLikeCountDto(restaurantLikeCountDto)
                    .restaurantImageDtoList(restaurantImageDtoList)
                    .restaurantMenuDtoList(restaurantMenuDtoList)
                    .build();

            return ResponseEntity.status(200).body(restaurantDetailDto);
        }
    }

    // 식당 정보 저장
//    public ResponseEntity<?> saveRestaurant(RestaurantSaveDto restaurantSaveDto, List<MultipartFile> multipartFileList) throws IOException {
//
//        // 동엔티티 설정
//        DongEntity dong = dongRepository.findByDongName(restaurantSaveDto.getRestaurantAdministrativeDistrict().getDongName()).get();
//
//        RestaurantEntity restaurantEntity = RestaurantEntity.builder()
//                .restaurantName(restaurantSaveDto.getRestaurantName())
//                .restaurantTelNum(restaurantSaveDto.getRestaurantTelNum())
//                .restaurantAddress(restaurantSaveDto.getRestaurantAddress())
//                .restaurantOpenTime(restaurantSaveDto.getRestaurantOpenTime())
//                .restaurantBreakTime(restaurantSaveDto.getRestaurantBreakTime())
//                .restaurantWebSite(restaurantSaveDto.getRestaurantWebSite())
//                .restaurantStatus("OPEN")
//                .dongEntity(dong)
//                .build();
//
//        Long restaurantId = restaurantRepository.save(restaurantEntity).getRestaurantId();
//
//        List<RestaurantMenuDto> restaurantMenuDtoList = restaurantSaveDto.getRestaurantMenuDtoList();
//        List<RestaurantMenuEntity> restaurantMenuEntityList = new ArrayList<>();
//
//        for (RestaurantMenuDto restaurantMenuDto : restaurantMenuDtoList) {
//            RestaurantMenuEntity restaurantMenuEntity = RestaurantMenuEntity
//                    .builder()
//                    .restaurantMenuName(restaurantMenuDto.getRestaurantMenuName())
//                    .restaurantMenuPrice(restaurantMenuDto.getRestaurantMenuPrice())
//                    .restaurantEntity(restaurantRepository.findByRestaurantId(restaurantId).get())
//                    .build();
//            restaurantMenuEntityList.add(restaurantMenuEntity);
//        }
//
//        restaurantMenuRepository.saveAll(restaurantMenuEntityList);
//
//        if(multipartFileList != null) {
//        String dirName = "restaurant" + restaurantId;
//            imageService.upload(multipartFileList, dirName, restaurantEntity);
//        }
//
//
//        if (restaurantRepository.findById(restaurantId).isEmpty()) {
//            return ResponseEntity.status(400).body("식당 등록에 실패하였습니다.");
//        } else {
//            return ResponseEntity.status(200).body("식당 등록에 성공하였습니다.");
//        }
//
//    }

    // 식당 검색 - 리뷰 많은순, 별점 순 정렬 필요, 좋아요 순 완료
    public ResponseEntity<Page<RestaurantDto>> findRestaurantByKeyword (int page, String sort, String keyword, String city, String district, String dong) {
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
                restaurantEntityPage = restaurantRepository.findByRestaurantNameAndDongEntity_DongName(pageable,keyword,dong);
            } else if (district != null) { // 구 이름 + 키워드 검색
                restaurantEntityPage = restaurantRepository.findByRestaurantNameAndDongEntity_DistrictsEntity_DistrictsName(pageable,keyword,district);
            } else if (city != null) { // 시 이름 + 키워드 검색
                restaurantEntityPage = restaurantRepository.findByRestaurantNameAndDongEntity_DistrictsEntity_CityEntity_CityName(pageable, keyword, city);
            } else { // 키워드 검색
                restaurantEntityPage = restaurantRepository.findByRestaurantNameAndLikeCountEntity_likeStatus(pageable,keyword);
            }
        } else {
//            String properties = sort.equals("like") ? "likeCountEntityList.size" : "reviewEntityList.size";
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
        }

        restaurantDtoPage = restaurantEntityPage
                .map(this::restaurantEntityToRestaurantDto);
        return ResponseEntity.status(200).body(restaurantDtoPage);
    }

    // 지역 검색
    public ResponseEntity<Page<RestaurantDto>> findRestaurantByAddress(String add, int page) {
        Pageable pageable = PageRequest.of(page,10);
        Page<RestaurantDto> restaurantDtoPage;
        Page<RestaurantEntity> restaurantEntityPage= restaurantRepository.findByAddress(pageable,add);
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

//    // 식당 정보 수정
//    public ResponseEntity<?> updateRestaurantInfo(Long restaurantId, UpdateRestaurantInfoDto restaurantDto, List<MultipartFile> multipartFileList) {
//        Optional<RestaurantEntity> restaurantEntityOptional = restaurantRepository.findById(restaurantId);
//        if (restaurantEntityOptional.isEmpty()) {
//            return ResponseEntity.status(200).body("restaurantId가 존재하지 않습니다.");
//        } else {
//            RestaurantEntity restaurantEntity = restaurantEntityOptional.get();
//
//            // 동엔티티 수정
//            DongEntity dong = dongRepository.findByDongName(restaurantDto.getRestaurantAdministrativeDistrict().getDongName()).get();
//
//            // 식당 정보 수정
//            restaurantEntity.setRestaurantName(restaurantDto.getRestaurantName());
//            restaurantEntity.setRestaurantTelNum(restaurantDto.getRestaurantTelNum());
//            restaurantEntity.setRestaurantAddress(restaurantDto.getRestaurantAddress());
//            restaurantEntity.setRestaurantOpenTime(restaurantDto.getRestaurantOpenTime());
//            restaurantEntity.setRestaurantBreakTime(restaurantDto.getRestaurantBreakTime());
//            restaurantEntity.setRestaurantWebSite(restaurantDto.getRestaurantWebSite());
//            restaurantEntity.setRestaurantStatus(restaurantDto.getRestaurantStatus());
//            restaurantEntity.setDongEntity(dong);
//
//            // 기존 메뉴 엔티티 삭제
//            List<RestaurantMenuEntity> existingMenuEntityList = restaurantEntity.getRestaurantMenuEntity();
//            restaurantMenuRepository.deleteAll(existingMenuEntityList);//  기존 메뉴 엔티티 리스트 삭제
//            restaurantEntity.getRestaurantMenuEntity().clear();
//
//            // 수정할 메뉴 dto -> entity 변환
//            List<RestaurantMenuEntity> restaurantMenuEntityList = new ArrayList<>();
//            List<RestaurantMenuUpdateDto> restaurantMenuDtoList = restaurantDto.getRestaurantMenuDtoList();
//
//            for (RestaurantMenuUpdateDto restaurantMenuUpdateDto : restaurantMenuDtoList) {
//                RestaurantMenuEntity restaurantMenuEntity = RestaurantMenuEntity
//                        .builder()
//                        .restaurantMenuName(restaurantMenuUpdateDto.getRestaurantMenuName())
//                        .restaurantMenuPrice(restaurantMenuUpdateDto.getRestaurantMenuPrice())
//                        .restaurantEntity(restaurantEntity)
//                        .build();
//                restaurantMenuEntityList.add(restaurantMenuEntity);
//            }
//
//            // 수정 후 메뉴 엔티티 리스트 저장 후 레스토랑에 연결
//            restaurantMenuRepository.saveAll(restaurantMenuEntityList);
//            restaurantEntity.setRestaurantMenuEntity(restaurantMenuEntityList);
//
//            // 대표 이미지 수정
//            if(multipartFileList != null) {
//                List<RestaurantImageEntity> restaurantImageEntityList = restaurantImageRepository.findAllByRestaurantEntity_RestaurantId(restaurantId);
//                for(RestaurantImageEntity restaurantImageEntity : restaurantImageEntityList) {
//                    String originImagePath = restaurantImageEntity.getRestaurantOriginUrl();
//                    String resizedImagePath = restaurantImageEntity.getRestaurantResizeUrl();
//
//                    int originStartIndex = originImagePath.indexOf("restaurant" + restaurantId + "/");
//                    String originFileName = originImagePath.substring(originStartIndex);
//                    imageService.deleteS3File(originFileName);
//
//                    int resizedStartIndex = resizedImagePath.indexOf("restaurant" + restaurantId + "/");
//                    String resizedFileName = resizedImagePath.substring(resizedStartIndex);
//                    imageService.deleteS3File(resizedFileName);
//                }
//                restaurantImageRepository.deleteAll(restaurantImageEntityList);
//
//                String dirName = "restaurant" + restaurantId;
//                imageService.upload(multipartFileList, dirName, restaurantEntity);
//            }
//
//            // 변경된 내용을 저장
//            restaurantRepository.save(restaurantEntity);
//
//            return ResponseEntity.status(200).body("식당 정보 및 메뉴 엔티티가 업데이트되었습니다.");
//        }

}