package com.hamtaro.sunflowerplate.dto.member;

import com.hamtaro.sunflowerplate.dto.ReviewImageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateReviewDto {

    private String reviewContent;

    private List<MultipartFile> imageFile;

}
