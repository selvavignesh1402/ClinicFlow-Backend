package com.HospitalManagement.controller;

import com.HospitalManagement.requestdto.AppointmentRequestDto;
import com.HospitalManagement.responsedto.AppointmentResponseDto;
import com.HospitalManagement.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('RECEPTION', 'CLINICIAN', 'ADMIN', 'CLINIC_MANAGER')")
    public List<AppointmentResponseDto> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('RECEPTION', 'CLINICIAN', 'ADMIN', 'CLINIC_MANAGER', 'PATIENT')")
    public AppointmentResponseDto getAppointmentById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyAuthority('RECEPTION', 'CLINICIAN', 'ADMIN', 'CLINIC_MANAGER', 'PATIENT')")
    public List<AppointmentResponseDto> getAppointmentsByPatient(@PathVariable Long patientId) {
        return appointmentService.getAppointmentsByPatient(patientId);
    }

    @GetMapping("/clinician/{clinicianId}")
    @PreAuthorize("hasAnyAuthority('RECEPTION', 'CLINICIAN', 'ADMIN', 'CLINIC_MANAGER')")
    public List<AppointmentResponseDto> getClinicianAppointments(
            @PathVariable Long clinicianId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return appointmentService.getClinicianAppointments(clinicianId, from, to);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('RECEPTION', 'ADMIN', 'PATIENT')")
    public AppointmentResponseDto createAppointment(@Valid @RequestBody AppointmentRequestDto request) {
        return appointmentService.createAppointment(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('RECEPTION', 'ADMIN')")
    public AppointmentResponseDto updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentRequestDto request
    ) {
        return appointmentService.updateAppointment(id, request);
    }

    @PatchMapping("/{id}/check-in")
    @PreAuthorize("hasAnyAuthority('RECEPTION', 'ADMIN')")
    public AppointmentResponseDto checkInAppointment(@PathVariable Long id) {
        return appointmentService.checkInAppointment(id);
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAnyAuthority('RECEPTION', 'CLINICIAN', 'ADMIN')")
    public AppointmentResponseDto completeAppointment(@PathVariable Long id) {
        return appointmentService.completeAppointment(id);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyAuthority('RECEPTION', 'ADMIN', 'PATIENT')")
    public AppointmentResponseDto cancelAppointment(@PathVariable Long id) {
        return appointmentService.cancelAppointment(id);
    }
}