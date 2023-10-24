package com.hamtaro.sunflowerplat.service;

import com.hamtaro.sunflowerplat.dto.AdminReportDto;
import com.hamtaro.sunflowerplat.dto.ReportDto;
import com.hamtaro.sunflowerplat.entity.member.MemberEntity;
import com.hamtaro.sunflowerplat.entity.review.ReportEntity;
import com.hamtaro.sunflowerplat.repository.MemberRepository;
import com.hamtaro.sunflowerplat.repository.ReportRepository;
import com.hamtaro.sunflowerplat.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class AdminService {

    private final MemberRepository memberRepository;
    private final ReportRepository reportRepository;
    private final RequestRepository requestRepository;


    //관리자 신고내역 조회
    public ResponseEntity<?> adminReportCheck(ReportDto reportDto ,String memberId){


        List<ReportEntity> reportEntityList = reportRepository.findByMemberEntity_MemberId(Long.valueOf(memberId));

        List<ReportDto> reportDtoList = new ArrayList<>();
        for(ReportEntity reportEntity : reportEntityList){
            ReportDto reportDtos = ReportDto.builder()
                    .reviewId(reportEntity.getReportId())
                    .reportCategory(reportEntity.getReportCategory())
                    .reportAt(reportEntity.getReportAt())
                    .memberId(reportEntity.getReportId())
                    .build();

            reportDtoList.add(reportDtos);
        }

        return ResponseEntity.status(200).body(reportDtoList);


    }
}
