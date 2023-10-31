package com.hamtaro.sunflowerplate.controller.restaurant;


import com.hamtaro.sunflowerplate.dto.ReportDto;
import com.hamtaro.sunflowerplate.dto.admin.RestaurantSaveDto;
import com.hamtaro.sunflowerplate.dto.admin.UpdateRestaurantInfoDto;
import com.hamtaro.sunflowerplate.service.AdminService;
import com.hamtaro.sunflowerplate.service.restaurant.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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
    public ResponseEntity<?> saveRestaurantInfo(@RequestPart(value = "data") RestaurantSaveDto restaurantSaveDto,
                                                @RequestPart(name = "file") List<MultipartFile> multipartFilelist) throws IOException {
        return restaurantService.saveRestaurant(restaurantSaveDto, multipartFilelist);
    }

    @PutMapping(consumes = {"multipart/form-data"}, value = "/restaurant/{restaurantId}")
    public ResponseEntity<?> updateRestaurantInfo(@PathVariable Long restaurantId ,
                                                  @RequestPart(value = "data") UpdateRestaurantInfoDto restaurantDto,
                                                  @RequestPart(name = "file") List<MultipartFile> multipartFilelist) throws IOException {
        return restaurantService.updateRestaurantInfo(restaurantId, restaurantDto, multipartFilelist);
    }




}
