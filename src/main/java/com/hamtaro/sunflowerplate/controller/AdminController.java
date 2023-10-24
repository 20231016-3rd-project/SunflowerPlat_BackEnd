package com.hamtaro.sunflowerplate.controller;


import com.hamtaro.sunflowerplate.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("sunflowerPlate/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    @DeleteMapping("/review/delete")
    public ResponseEntity<?> removeAdminReview(@RequestParam Long reviewId){
        return adminService.deleteAdminReview(reviewId);
    }

}
