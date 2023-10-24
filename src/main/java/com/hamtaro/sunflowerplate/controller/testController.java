package com.hamtaro.sunflowerplate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class testController {
    @GetMapping("/check")
    public String checkServerStatus(){
        return "check";
    }
}
