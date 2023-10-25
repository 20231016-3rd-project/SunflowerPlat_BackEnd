package com.hamtaro.sunflowerplate.controller;


import com.hamtaro.sunflowerplate.dto.ReportDto;
import com.hamtaro.sunflowerplate.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Log4j2
@RestController
@RequestMapping("/sunflowerPlate/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @DeleteMapping("/review/delete")
    public ResponseEntity<?> removeAdminReview(@RequestParam Long reviewId){
        return adminService.deleteAdminReview(reviewId);
    }

    //관리자 신고 확인
    @GetMapping("/review/")
    public ResponseEntity<?> reviewReport(){


        return adminService.adminReportCheck();

    }

    //관리자 식당 정보 수정 요청 확인
    @GetMapping("/restaurant/edit/")
    public ResponseEntity<?> requestRestaurant(){

        return adminService.adminRestaurantModifyCheck();
    }


}
