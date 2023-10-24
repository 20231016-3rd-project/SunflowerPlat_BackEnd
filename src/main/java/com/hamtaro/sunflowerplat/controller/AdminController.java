package com.hamtaro.sunflowerplat.controller;


import com.hamtaro.sunflowerplat.dto.AdminReportDto;
import com.hamtaro.sunflowerplat.dto.ReportDto;
import com.hamtaro.sunflowerplat.service.AdminService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("sunflowerPlate/admin")

@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/review")
    public ResponseEntity<?> reviewReport(@RequestBody ReportDto reportDto , String memberId){


        return adminService.adminReportCheck(reportDto,memberId);

    }


}
