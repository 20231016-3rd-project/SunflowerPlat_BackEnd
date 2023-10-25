package com.hamtaro.sunflowerplate.controller;


import com.hamtaro.sunflowerplate.dto.ReportDto;
import com.hamtaro.sunflowerplate.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/review")
    public ResponseEntity<?> reviewReport(@RequestBody ReportDto reportDto , String memberId){


        return adminService.adminReportCheck(reportDto,memberId);
    }
}
