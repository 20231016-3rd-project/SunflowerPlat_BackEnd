package com.hamtaro.sunflowerplate.controller;


import com.hamtaro.sunflowerplate.dto.ReportDto;
import com.hamtaro.sunflowerplate.dto.admin.RestaurantSaveDto;
import com.hamtaro.sunflowerplate.dto.admin.UpdateRestaurantInfoDto;
import com.hamtaro.sunflowerplate.service.AdminService;
import com.hamtaro.sunflowerplate.service.RestaurantService;
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
    private final RestaurantService restaurantService;

    @DeleteMapping("/review/delete")
    public ResponseEntity<?> removeAdminReview(@RequestParam Long reviewId){
        return adminService.deleteAdminReview(reviewId);
    }

    @GetMapping("/review")
    public ResponseEntity<?> reviewReport(@RequestBody ReportDto reportDto , String memberId){


        return adminService.adminReportCheck(reportDto,memberId);

    }

    @PostMapping( consumes = {"multipart/form-data"}, value="/restaurant/registration")
    public ResponseEntity<?> saveRestaurantInfo(@RequestPart(value = "data") RestaurantSaveDto restaurantSaveDto){
        return restaurantService.saveRestaurant(restaurantSaveDto);
    }

    @PutMapping(consumes = {"multipart/form-data"}, value = "/restaurant/{restaurantId}")
    public ResponseEntity<?> updateRestaurantInfo(@PathVariable Long restaurantId ,
                                                  @RequestPart(value = "data") UpdateRestaurantInfoDto restaurantDto){
        return restaurantService.updateRestaurantInfo(restaurantId, restaurantDto);
    }




}
