package com.hamtaro.sunflowerplate.controller;


import com.hamtaro.sunflowerplate.dto.admin.RestaurantSaveDto;
import com.hamtaro.sunflowerplate.dto.admin.UpdateRestaurantInfoDto;
import com.hamtaro.sunflowerplate.jwt.config.TokenProvider;
import com.hamtaro.sunflowerplate.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/sunflowerPlate/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final TokenProvider tokenProvider;

    @DeleteMapping("/review/delete")
    public ResponseEntity<?> removeAdminReview(@RequestParam Long reviewId, HttpServletRequest request){
        String header = request.getHeader(tokenProvider.loginAccessToken);
        String userId = tokenProvider.getUserPk(header);

        return adminService.deleteAdminReview(reviewId, userId);
    }

    //관리자 신고 확인
    @GetMapping("/review/")
    public ResponseEntity<?> reviewReport(HttpServletRequest request) {

        String header = request.getHeader(tokenProvider.loginAccessToken);
        String userId = tokenProvider.getUserPk(header);

        return adminService.adminReportCheck(userId);
    }

    //관리자 식당 정보 수정 요청 확인
    @GetMapping("/restaurant/edit/")
    public ResponseEntity<?> requestRestaurant(HttpServletRequest request){

        String header = request.getHeader(tokenProvider.loginAccessToken);
        String userId = tokenProvider.getUserPk(header);

        return adminService.adminRestaurantModifyCheck(userId);
    }

    // 식당 정보 등록
    @PostMapping( consumes = {"multipart/form-data"}, value="/restaurant/registration")
    public ResponseEntity<?> saveRestaurantInfo(HttpServletRequest request,
                                                @RequestPart(value = "data") RestaurantSaveDto restaurantSaveDto,
                                                @RequestPart(name = "file") List<MultipartFile> multipartFilelist) throws IOException {
        String header = request.getHeader(tokenProvider.loginAccessToken);
        String userId = tokenProvider.getUserPk(header);
        return adminService.saveRestaurant(restaurantSaveDto, multipartFilelist);
    }

    // 식당 정보 수정
    @PutMapping(consumes = {"multipart/form-data"}, value = "/restaurant/{restaurantId}")
    public ResponseEntity<?> updateRestaurantInfo(HttpServletRequest request,
                                                  @PathVariable Long restaurantId ,
                                                  @RequestPart(value = "data") UpdateRestaurantInfoDto restaurantDto,
                                                  @RequestPart(name = "file") List<MultipartFile> multipartFilelist) throws IOException {
        String header = request.getHeader(tokenProvider.loginAccessToken);
        String userId = tokenProvider.getUserPk(header);
        return adminService.updateRestaurantInfo(restaurantId, restaurantDto, multipartFilelist);
    }


}
