package com.HospitalManagement.controller;

import com.HospitalManagement.requestdto.EncounterRequestDto;
import com.HospitalManagement.responsedto.EncounterResponseDto;
import com.HospitalManagement.service.EncounterService;
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
@RequestMapping("/api/v1/clinician/encounters")
public class EncounterController {

    private final EncounterService encounterService;

    public EncounterController(EncounterService encounterService) {
        this.encounterService = encounterService;
    }

    @GetMapping
    public List<EncounterResponseDto> getAllEncounters() {
        return encounterService.getAllEncounters();
    }

    @GetMapping("/{encounterId}")
    public EncounterResponseDto getEncounterById(@PathVariable Long encounterId) {
        return encounterService.getEncounterById(encounterId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EncounterResponseDto createEncounter(@Valid @RequestBody EncounterRequestDto requestDto) {
        return encounterService.createEncounter(requestDto);
    }

    @PutMapping("/{encounterId}")
    public EncounterResponseDto updateEncounter(
            @PathVariable Long encounterId,
            @Valid @RequestBody EncounterRequestDto requestDto
    ) {
        return encounterService.updateEncounter(encounterId, requestDto);
    }

    @DeleteMapping("/{encounterId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEncounter(@PathVariable Long encounterId) {
        encounterService.deleteEncounter(encounterId);
    }
}
