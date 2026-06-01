package com.HospitalManagement.service;

import com.HospitalManagement.entity.Report;
import com.HospitalManagement.entity.User;
import com.HospitalManagement.exception.ResourceNotFoundException;
import com.HospitalManagement.repository.ReportRepository;
import com.HospitalManagement.repository.UserRepository;
import com.HospitalManagement.requestdto.ReportRequestDto;
import com.HospitalManagement.responsedto.ReportResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Report Service Tests")
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReportService reportService;

    private User currentUser;
    private Report report;
    private ReportRequestDto requestDto;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        currentUser = User.builder()
                .userId(1L)
                .name("Alice Manager")
                .email("alice@hospital.com")
                .build();

        report = new Report();
        report.setReportId(10L);
        report.setScope("OPD");
        report.setParametersJson("{}");
        report.setMetricsJson("{}");
        report.setGeneratedAt(now);
        report.setGeneratedBy(currentUser);
        report.setReportUri("/reports/opd-10.pdf");

        requestDto = new ReportRequestDto(
                "OPD",
                "{}",
                "{}",
                now,
                "/reports/opd-10.pdf"
        );

    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should retrieve all reports successfully")
    void testGetAllReports() {
        // Arrange
        when(reportRepository.findAll()).thenReturn(Arrays.asList(report));

        // Act
        List<ReportResponseDto> result = reportService.getAllReports();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("/reports/opd-10.pdf", result.get(0).reportUri());
        verify(reportRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve report by ID successfully")
    void testGetReportById() {
        // Arrange
        when(reportRepository.findById(10L)).thenReturn(Optional.of(report));

        // Act
        ReportResponseDto result = reportService.getReportById(10L);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.reportId());
        assertEquals("OPD", result.scope());
        verify(reportRepository, times(1)).findById(10L);
    }

    @Test
    @DisplayName("Should throw exception when report not found by ID")
    void testGetReportNotFound() {
        // Arrange
        when(reportRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reportService.getReportById(999L));
        assertEquals("Report not found with id: 999", exception.getMessage());
    }

    @Test
    @DisplayName("Should create report successfully with logged-in user override")
    void testCreateReport() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("alice@hospital.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByEmail("alice@hospital.com")).thenReturn(Optional.of(currentUser));
        when(reportRepository.save(any(Report.class))).thenReturn(report);

        // Act
        ReportResponseDto result = reportService.createReport(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals("OPD", result.scope());
        assertEquals("Alice Manager", result.generatedByName());
        verify(reportRepository, times(1)).save(any(Report.class));
    }

    @Test
    @DisplayName("Should update report successfully")
    void testUpdateReport() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("alice@hospital.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByEmail("alice@hospital.com")).thenReturn(Optional.of(currentUser));
        when(reportRepository.findById(10L)).thenReturn(Optional.of(report));
        when(reportRepository.save(any(Report.class))).thenReturn(report);

        // Act
        ReportResponseDto result = reportService.updateReport(10L, requestDto);

        // Assert
        assertNotNull(result);
        verify(reportRepository, times(1)).save(any(Report.class));
    }

    @Test
    @DisplayName("Should delete report successfully")
    void testDeleteReport() {
        // Arrange
        when(reportRepository.findById(10L)).thenReturn(Optional.of(report));
        doNothing().when(reportRepository).delete(any(Report.class));

        // Act
        reportService.deleteReport(10L);

        // Assert
        verify(reportRepository, times(1)).delete(report);
    }
}
