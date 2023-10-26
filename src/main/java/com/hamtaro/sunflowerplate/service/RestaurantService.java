package com.hamtaro.sunflowerplate.service;

import com.hamtaro.sunflowerplate.dto.admin.RestaurantMenuUpdateDto;
import com.hamtaro.sunflowerplate.dto.admin.RestaurantSaveDto;
import com.hamtaro.sunflowerplate.dto.admin.RestaurantMenuDto;
import com.hamtaro.sunflowerplate.dto.admin.UpdateRestaurantInfoDto;
import com.hamtaro.sunflowerplate.dto.restaurant.RestaurantDetailDto;
import com.hamtaro.sunflowerplate.dto.restaurant.RestaurantDto;
import com.hamtaro.sunflowerplate.entity.address.DongEntity;
import com.hamtaro.sunflowerplate.entity.restaurant.RestaurantEntity;
import com.hamtaro.sunflowerplate.entity.restaurant.RestaurantMenuEntity;
import com.hamtaro.sunflowerplate.repository.DongRepository;
import com.hamtaro.sunflowerplate.repository.RestaurantMenuRepository;
import com.hamtaro.sunflowerplate.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final DongRepository dongRepository;
    private final RestaurantMenuRepository restaurantMenuRepository;

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

    public ResponseEntity<?> saveRestaurant(RestaurantSaveDto restaurantSaveDto) {

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

        if (restaurantRepository.findById(restaurantId).isEmpty()) {
            return ResponseEntity.status(400).body("식당 등록에 실패하였습니다.");
        } else {
            return ResponseEntity.status(200).body("식당 등록에 성공하였습니다.");
        }

    }

    public ResponseEntity<Page<RestaurantDto>> findRestaurantByKeyword(String keyword, int page) {
        Pageable pageable = PageRequest.of(page,10);
        Page<RestaurantDto> restaurantDtoPage;
        Page<RestaurantEntity> restaurantEntityPage= restaurantRepository.findByRestaurantName(pageable,keyword);

        restaurantDtoPage = restaurantEntityPage
                .map(this::restaurantEntityToRestaurantDto);
        return ResponseEntity.status(200).body(restaurantDtoPage);
    }

    public ResponseEntity<Page<RestaurantDto>> findRestaurantByAddress(String add, int page) {
        Pageable pageable = PageRequest.of(page,10);
        Page<RestaurantDto> restaurantDtoPage;
        Page<RestaurantEntity> restaurantEntityPage= restaurantRepository.findByAddress(pageable,add);
        restaurantDtoPage = restaurantEntityPage
                .map(this::restaurantEntityToRestaurantDto);
        return ResponseEntity.status(200).body(restaurantDtoPage);
    }

    private RestaurantDto restaurantEntityToRestaurantDto(RestaurantEntity restaurantEntity){
        RestaurantDto restaurantDto = RestaurantDto
                .builder()
                .restaurantId(restaurantEntity.getRestaurantId())
                .restaurantName(restaurantEntity.getRestaurantName())
                .restaurantAddress(restaurantEntity.getRestaurantAddress())
                .restaurantWebSite(restaurantEntity.getRestaurantWebSite())
                .build();
        return restaurantDto;
    }



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