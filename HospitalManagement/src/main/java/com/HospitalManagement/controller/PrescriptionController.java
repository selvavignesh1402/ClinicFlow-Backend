package com.HospitalManagement.controller;

import com.HospitalManagement.requestdto.PrescriptionRequestDto;
import com.HospitalManagement.responsedto.PrescriptionResponseDto;
import com.HospitalManagement.service.PrescriptionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/v1/pharmacist/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @GetMapping
    public List<PrescriptionResponseDto> getAllPrescriptions() {
        return prescriptionService.getAllPrescriptions();
    }

    @GetMapping("/{rxId}")
    public PrescriptionResponseDto getPrescriptionById(@PathVariable Long rxId) {
        return prescriptionService.getPrescriptionById(rxId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrescriptionResponseDto createPrescription(@Valid @RequestBody PrescriptionRequestDto requestDto) {
        return prescriptionService.createPrescription(requestDto);
    }

    @PutMapping("/{rxId}")
    public PrescriptionResponseDto updatePrescription(
            @PathVariable Long rxId,
            @Valid @RequestBody PrescriptionRequestDto requestDto
    ) {
        return prescriptionService.updatePrescription(rxId, requestDto);
    }

    @DeleteMapping("/{rxId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePrescription(@PathVariable Long rxId) {
        prescriptionService.deletePrescription(rxId);
    }
}
