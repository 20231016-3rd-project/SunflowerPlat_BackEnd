package com.hamtaro.sunflowerplate.service.restaurant;

import com.hamtaro.sunflowerplate.dto.admin.RestaurantMenuUpdateDto;
import com.hamtaro.sunflowerplate.dto.admin.RestaurantSaveDto;
import com.hamtaro.sunflowerplate.dto.admin.RestaurantMenuDto;
import com.hamtaro.sunflowerplate.dto.admin.UpdateRestaurantInfoDto;
import com.hamtaro.sunflowerplate.dto.restaurant.RestaurantDetailDto;
import com.hamtaro.sunflowerplate.dto.restaurant.RestaurantDto;
import com.hamtaro.sunflowerplate.entity.address.DongEntity;
import com.hamtaro.sunflowerplate.entity.restaurant.RestaurantEntity;
import com.hamtaro.sunflowerplate.entity.restaurant.RestaurantMenuEntity;
import com.hamtaro.sunflowerplate.repository.restaurant.DongRepository;
import com.hamtaro.sunflowerplate.repository.restaurant.LikeCountRepository;
import com.hamtaro.sunflowerplate.repository.restaurant.RestaurantMenuRepository;
import com.hamtaro.sunflowerplate.repository.restaurant.RestaurantRepository;
import com.hamtaro.sunflowerplate.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final DongRepository dongRepository;
    private final RestaurantMenuRepository restaurantMenuRepository;
    private final LikeCountRepository likeCountRepository;
    private final MemberRepository memberRepository;
    private final ImageService imageService;

    // 이미지 S3 추가 필요

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

            RestaurantDetailDto restaurantDetailDto = RestaurantDetailDto
                    .builder()
                    .restaurantName(restaurantEntity.getRestaurantName())
                    .restaurantTelNum(restaurantEntity.getRestaurantTelNum())
                    .restaurantAddress(restaurantEntity.getRestaurantAddress())
                    .restaurantOpenTime(restaurantEntity.getRestaurantOpenTime())
                    .restaurantBreakTime(restaurantEntity.getRestaurantBreakTime())
                    .restaurantMenuDtoList(restaurantMenuDtoList)
                    .build();

            return ResponseEntity.status(200).body(restaurantDetailDto);
        }
    }

    // 식당 정보 저장
    public ResponseEntity<?> saveRestaurant(RestaurantSaveDto restaurantSaveDto, List<MultipartFile> multipartFilelist) throws IOException {

        // 동엔티티 설정
        DongEntity dong = dongRepository.findByDongName(restaurantSaveDto.getRestaurantAdministrativeDistrict().getDongName()).get();

        RestaurantEntity restaurantEntity = RestaurantEntity.builder()
                .restaurantName(restaurantSaveDto.getRestaurantName())
                .restaurantTelNum(restaurantSaveDto.getRestaurantTelNum())
                .restaurantAddress(restaurantSaveDto.getRestaurantAddress())
                .restaurantOpenTime(restaurantSaveDto.getRestaurantOpenTime())
                .restaurantBreakTime(restaurantSaveDto.getRestaurantBreakTime())
                .restaurantWebSite(restaurantSaveDto.getRestaurantWebSite())
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

        if(multipartFilelist != null) {
            imageService.upload(multipartFilelist, restaurantEntity);
        }


        if (restaurantRepository.findById(restaurantId).isEmpty()) {
            return ResponseEntity.status(400).body("식당 등록에 실패하였습니다.");
        } else {
            return ResponseEntity.status(200).body("식당 등록에 성공하였습니다.");
        }

    }

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
                .likeCount(likeCountRepository.countByRestaurantEntity_RestaurantId(restaurantEntity.getRestaurantId()))
                .reviewCount(restaurantEntity.getReviewEntityList().size())
                .avgStarRate(restaurantRepository.findStarRateByRestaurantId(restaurantEntity.getRestaurantId()))
                .build();
    }

    // 식당 정보 수정
    public ResponseEntity<?> updateRestaurantInfo(Long restaurantId, UpdateRestaurantInfoDto restaurantDto) {
        Optional<RestaurantEntity> restaurantEntityOptional = restaurantRepository.findById(restaurantId);
        if (restaurantEntityOptional.isEmpty()) {
            return ResponseEntity.status(200).body("restaurantId가 존재하지 않습니다.");
        } else {
            RestaurantEntity restaurantEntity = restaurantEntityOptional.get();

            DongEntity dong = dongRepository.findByDongName(restaurantDto.getRestaurantAdministrativeDistrict().getDongName()).get();

            restaurantEntity.setRestaurantName(restaurantDto.getRestaurantName());
            restaurantEntity.setRestaurantTelNum(restaurantDto.getRestaurantTelNum());
            restaurantEntity.setRestaurantAddress(restaurantDto.getRestaurantAddress());
            restaurantEntity.setRestaurantOpenTime(restaurantDto.getRestaurantOpenTime());
            restaurantEntity.setRestaurantBreakTime(restaurantDto.getRestaurantBreakTime());
            restaurantEntity.setRestaurantWebSite(restaurantDto.getRestaurantWebSite());
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

            // 변경된 내용을 저장
            restaurantRepository.save(restaurantEntity);

            return ResponseEntity.status(200).body("식당 정보 및 메뉴 엔티티가 업데이트되었습니다.");

        }
    }
}