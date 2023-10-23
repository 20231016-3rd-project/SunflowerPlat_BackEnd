package com.hamtaro.sunflowerplat.repository;

import com.hamtaro.sunflowerplat.entity.review.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<ReportEntity,Long> {
    Optional<ReportEntity> findByReportId(Long useId);
}
