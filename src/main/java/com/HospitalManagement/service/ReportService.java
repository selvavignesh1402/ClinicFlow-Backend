package com.HospitalManagement.service;

import com.HospitalManagement.entity.Report;
import com.HospitalManagement.entity.User;
import com.HospitalManagement.exception.ResourceNotFoundException;
import com.HospitalManagement.repository.ReportRepository;
import com.HospitalManagement.repository.UserRepository;
import com.HospitalManagement.requestdto.ReportRequestDto;
import com.HospitalManagement.responsedto.ReportResponseDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ReportResponseDto> getAllReports() {
        logger.debug("Fetching all reports");
        List<Report> reportEntities = reportRepository.findAll();
        List<ReportResponseDto> reports = new ArrayList<>();

        for (Report report : reportEntities) {
            reports.add(toResponseDto(report));
        }

        logger.info("Retrieved {} reports", reports.size());
        return reports;
    }

    @Transactional(readOnly = true)
    public ReportResponseDto getReportById(Long reportId) {
        logger.debug("Fetching report with ID: {}", reportId);
        ReportResponseDto report = toResponseDto(findReport(reportId));
        logger.info("Retrieved report - ID: {}, Scope: {}", reportId, report.scope());
        return report;
    }

    public ReportResponseDto createReport(ReportRequestDto requestDto) {
        logger.info("Creating new report - Scope: {}", requestDto.scope());

        // Get current logged-in user from JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // usually email/username from JWT

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + email));

        Report report = new Report();
        mapRequestToEntity(requestDto, report);

        // Override generatedBy with logged-in user
        report.setGeneratedBy(currentUser);

        return toResponseDto(reportRepository.save(report));
    }

    public ReportResponseDto updateReport(Long reportId, ReportRequestDto requestDto) {
        logger.info("Updating report - ReportID: {}, NewScope: {}", reportId, requestDto.scope());
        Report report = findReport(reportId);
        mapRequestToEntity(requestDto, report);

        // Keep the original generatedBy or reassign to current user if needed
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + email));
        report.setGeneratedBy(currentUser);

        return toResponseDto(reportRepository.save(report));
    }

    public void deleteReport(Long reportId) {
        reportRepository.delete(findReport(reportId));
    }

    private void mapRequestToEntity(ReportRequestDto requestDto, Report report) {
        report.setScope(requestDto.scope());
        report.setParametersJson(requestDto.parametersJson());
        report.setMetricsJson(requestDto.metricsJson());
        report.setGeneratedAt(requestDto.generatedAt() != null ? requestDto.generatedAt() : LocalDateTime.now());
        report.setReportUri(requestDto.reportUri());
    }

    private Report findReport(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + reportId));
    }

    private ReportResponseDto toResponseDto(Report report) {
        User generatedBy = report.getGeneratedBy();
        return new ReportResponseDto(
                report.getReportId(),
                report.getScope(),
                report.getParametersJson(),
                report.getMetricsJson(),
                generatedBy != null ? generatedBy.getUserId() : null,
                generatedBy != null ? generatedBy.getName() : null,
                report.getGeneratedAt(),
                report.getReportUri()
        );
    }
}
