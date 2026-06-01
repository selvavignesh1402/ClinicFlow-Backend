package com.HospitalManagement.controller;

import com.HospitalManagement.requestdto.ReportRequestDto;
import com.HospitalManagement.responsedto.ReportResponseDto;
import com.HospitalManagement.service.ReportService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/manager/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('CLINIC_MANAGER', 'ADMIN', 'FINANCE_OFFICER')")
    public List<ReportResponseDto> getAllReports() {
        return reportService.getAllReports();
    }

    @GetMapping("/{reportId}")
    @PreAuthorize("hasAnyAuthority('CLINIC_MANAGER', 'ADMIN', 'FINANCE_OFFICER')")
    public ReportResponseDto getReportById(@PathVariable Long reportId) {
        return reportService.getReportById(reportId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('CLINIC_MANAGER', 'ADMIN')")
    public ReportResponseDto createReport(@Valid @RequestBody ReportRequestDto requestDto) {
        return reportService.createReport(requestDto);
    }

    @PutMapping("/{reportId}")
    @PreAuthorize("hasAnyAuthority('CLINIC_MANAGER', 'ADMIN')")
    public ReportResponseDto updateReport(
            @PathVariable Long reportId,
            @Valid @RequestBody ReportRequestDto requestDto
    ) {
        return reportService.updateReport(reportId, requestDto);
    }

    @DeleteMapping("/{reportId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteReport(@PathVariable Long reportId) {
        reportService.deleteReport(reportId);
    }
}