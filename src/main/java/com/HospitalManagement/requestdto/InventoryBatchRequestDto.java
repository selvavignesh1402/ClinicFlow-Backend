package com.HospitalManagement.requestdto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryBatchRequestDto {

    private Long medId;
    private String batchNumber;
    private Integer quantity;
    private String unit;
    private LocalDate expiryDate;
    private String location;
    private Double costPrice;
}