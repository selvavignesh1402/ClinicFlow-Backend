package com.HospitalManagement.controller;

import com.HospitalManagement.requestdto.PharmacyRequestDto;
import com.HospitalManagement.responsedto.InventoryResponseDto;
import com.HospitalManagement.responsedto.StockSummaryResponseDto;
import com.HospitalManagement.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pharmacist/inventory")
@PreAuthorize("hasAnyAuthority('PHARMACY', 'ADMIN', 'CLINIC_MANAGER')")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public List<InventoryResponseDto> getAllInventory() {
        return inventoryService.getAllInventory();
    }

    @GetMapping("/{inventoryId}")
    public InventoryResponseDto getInventoryItemById(@PathVariable Long inventoryId) {
        return inventoryService.getInventoryItemById(inventoryId);
    }

    @GetMapping("/medication/{medicationId}")
    public List<InventoryResponseDto> getInventoryByMedication(@PathVariable Long medicationId) {
        return inventoryService.getInventoryByMedication(medicationId);
    }

    @GetMapping("/summary")
    public List<StockSummaryResponseDto> getStockSummary() {
        return inventoryService.getStockSummary();
    }

    @GetMapping("/low-stock")
    public List<StockSummaryResponseDto> getLowStock(@RequestParam(required = false) Integer threshold) {
        return inventoryService.getLowStock(threshold);
    }

    @GetMapping("/expiring")
    public List<InventoryResponseDto> getExpiringInventory(@RequestParam(required = false) Integer days) {
        return inventoryService.getExpiringInventory(days);
    }

    @GetMapping("/expired")
    public List<InventoryResponseDto> getExpiredInventory() {
        return inventoryService.getExpiredInventory();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('PHARMACY', 'ADMIN')")
    public InventoryResponseDto createInventoryItem(@RequestBody PharmacyRequestDto request) {
        return inventoryService.createInventoryItem(request);
    }

    @PutMapping("/{inventoryId}")
    @PreAuthorize("hasAnyAuthority('PHARMACY', 'ADMIN')")
    public InventoryResponseDto updateInventoryItem(@PathVariable Long inventoryId, @RequestBody PharmacyRequestDto request) {
        return inventoryService.updateInventoryItem(inventoryId, request);
    }

    @PostMapping("/{inventoryId}/adjust")
    @PreAuthorize("hasAnyAuthority('PHARMACY', 'ADMIN')")
    public InventoryResponseDto adjustStock(@PathVariable Long inventoryId, @RequestBody PharmacyRequestDto request) {
        return inventoryService.adjustStock(inventoryId, request);
    }

    @DeleteMapping("/{inventoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteInventoryItem(@PathVariable Long inventoryId) {
        inventoryService.deleteInventoryItem(inventoryId);
    }
}