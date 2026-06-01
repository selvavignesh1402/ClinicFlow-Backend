package com.HospitalManagement.controller;

import com.HospitalManagement.requestdto.EncounterRequestDto;
import com.HospitalManagement.responsedto.EncounterResponseDto;
import com.HospitalManagement.service.EncounterService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clinician/encounters")
public class  EncounterController {

    private final EncounterService encounterService;

    public EncounterController(EncounterService encounterService) {
        this.encounterService = encounterService;
    }

    @GetMapping
    public List<EncounterResponseDto> getAllEncounters() {
        List<EncounterResponseDto> encounters = encounterService.getAllEncounters();
        return encounters;
    }
    @GetMapping("/{encounterId}")
    public EncounterResponseDto getEncounterById(@PathVariable Long encounterId) {
        EncounterResponseDto encounter = encounterService.getEncounterById(encounterId);
        return encounter;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EncounterResponseDto createEncounter(@Valid @RequestBody EncounterRequestDto requestDto) {
        EncounterResponseDto encounter = encounterService.createEncounter(requestDto);
        return encounter;
    }

    @PutMapping("/{encounterId}")
    public EncounterResponseDto updateEncounter(@PathVariable Long encounterId, @Valid @RequestBody EncounterRequestDto requestDto
    ) {
        EncounterResponseDto encounter = encounterService.updateEncounter(encounterId, requestDto);
        return encounter;
    }

    @PatchMapping("status/{encounterId}")
    public EncounterResponseDto completeEncounter(@PathVariable Long encounterId) {
        EncounterResponseDto response = encounterService.completeEncounter(encounterId);
        return response;
    }

    @DeleteMapping("/{encounterId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEncounter(@PathVariable Long encounterId) {
        encounterService.deleteEncounter(encounterId);
    }
}