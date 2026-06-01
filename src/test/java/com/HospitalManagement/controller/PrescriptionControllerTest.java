// package com.HospitalManagement.controller;

// import com.HospitalManagement.enums.PrescriptionStatus;
// import com.HospitalManagement.requestdto.PrescriptionRequestDto;
// import com.HospitalManagement.responsedto.PrescriptionResponseDto;
// import com.HospitalManagement.service.PrescriptionService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.DisplayName;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.web.servlet.MockMvc;
// import com.fasterxml.jackson.databind.ObjectMapper;

// import java.time.LocalDateTime;
// import java.util.Arrays;
// import java.util.List;

// import static org.hamcrest.Matchers.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @SpringBootTest
// @AutoConfigureMockMvc
// @DisplayName("Prescription Controller Tests")
// class PrescriptionControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private PrescriptionService prescriptionService;

//     @Autowired
//     private ObjectMapper objectMapper;

//     private PrescriptionRequestDto prescriptionRequestDto;
//     private PrescriptionResponseDto prescriptionResponseDto;
//     private LocalDateTime now;

//     @BeforeEach
//     void setUp() {
//         now = LocalDateTime.now();

//         prescriptionRequestDto = new PrescriptionRequestDto(
//                 1L,  // encounterId
//                 1L,  // patientId
//                 1L,  // medicationId
//                 "500mg",  // dosage
//                 "Twice daily",  // frequency
//                 10,  // durationDays
//                 20,  // quantity
//                 2,  // repeats
//                 "Oral",  // route
//                 "After meals",  // notes
//                 PrescriptionStatus.DRAFT  // status
//         );

//         prescriptionResponseDto = new PrescriptionResponseDto(
//                 1L,  // rxId
//                 1L,  // encounterId
//                 1L,  // patientId
//                 "John Doe",  // patientName
//                 1L,  // clinicianId
//                 "Dr. Smith",  // clinicianName
//                 1L,  // medicationId
//                 "Aspirin",  // medicationName
//                 "500mg",  // dosage
//                 "Twice daily",  // frequency
//                 10,  // durationDays
//                 20,  // quantity
//                 2,  // repeats
//                 "Oral",  // route
//                 "After meals",  // notes
//                 PrescriptionStatus.DRAFT,  // status
//                 now  // issuedAt
//         );
//     }

//     @Test
//     @DisplayName("Should get all prescriptions")
//     @WithMockUser(roles = "CLINICIAN")
//     void testGetAllPrescriptions() throws Exception {
//         // Arrange
//         List<PrescriptionResponseDto> prescriptions = Arrays.asList(prescriptionResponseDto);
//         when(prescriptionService.getAllPrescriptions()).thenReturn(prescriptions);

//         // Act & Assert
//         mockMvc.perform(get("/api/v1/prescriptions")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$", hasSize(1)))
//                 .andExpect(jsonPath("$[0].rxId", is(1)))
//                 .andExpect(jsonPath("$[0].dosage", is("500mg")))
//                 .andExpect(jsonPath("$[0].status", is("DRAFT")));

//         verify(prescriptionService, times(1)).getAllPrescriptions();
//     }

//     @Test
//     @DisplayName("Should get prescription by ID")
//     @WithMockUser(roles = "CLINICIAN")
//     void testGetPrescriptionById() throws Exception {
//         // Arrange
//         when(prescriptionService.getPrescriptionById(1L)).thenReturn(prescriptionResponseDto);

//         // Act & Assert
//         mockMvc.perform(get("/api/v1/prescriptions/1")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.rxId", is(1)))
//                 .andExpect(jsonPath("$.patientName", is("John Doe")))
//                 .andExpect(jsonPath("$.medicationName", is("Aspirin")))
//                 .andExpect(jsonPath("$.frequency", is("Twice daily")));

//         verify(prescriptionService, times(1)).getPrescriptionById(1L);
//     }

//     @Test
//     @DisplayName("Should create prescription successfully")
//     @WithMockUser(roles = "CLINICIAN")
//     void testCreatePrescription() throws Exception {
//         // Arrange
//         when(prescriptionService.createPrescription(any(PrescriptionRequestDto.class)))
//                 .thenReturn(prescriptionResponseDto);

//         // Act & Assert
//         mockMvc.perform(post("/api/v1/prescriptions")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(prescriptionRequestDto)))
//                 .andExpect(status().isCreated())
//                 .andExpect(jsonPath("$.rxId", is(1)))
//                 .andExpect(jsonPath("$.dosage", is("500mg")))
//                 .andExpect(jsonPath("$.status", is("DRAFT")));

//         verify(prescriptionService, times(1)).createPrescription(any(PrescriptionRequestDto.class));
//     }

//     @Test
//     @DisplayName("Should update prescription successfully")
//     @WithMockUser(roles = "CLINICIAN")
//     void testUpdatePrescription() throws Exception {
//         // Arrange
//         PrescriptionRequestDto updateDto = new PrescriptionRequestDto(
//                 1L, 1L, 1L, "1000mg", "Three times daily", 15, 30, 3, "Oral", "Updated notes", PrescriptionStatus.DRAFT
//         );

//         PrescriptionResponseDto updatedResponse = new PrescriptionResponseDto(
//                 1L, 1L, 1L, "John Doe", 1L, "Dr. Smith", 1L, "Aspirin",
//                 "1000mg", "Three times daily", 15, 30, 3, "Oral", "Updated notes", PrescriptionStatus.DRAFT, now
//         );

//         when(prescriptionService.updatePrescription(1L, updateDto)).thenReturn(updatedResponse);

//         // Act & Assert
//         mockMvc.perform(put("/api/v1/prescriptions/1")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(updateDto)))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.dosage", is("1000mg")))
//                 .andExpect(jsonPath("$.frequency", is("Three times daily")));

//         verify(prescriptionService, times(1)).updatePrescription(1L, updateDto);
//     }

//     @Test
//     @DisplayName("Should update prescription status")
//     @WithMockUser(roles = "CLINICIAN")
//     void testUpdatePrescriptionStatus() throws Exception {
//         // Arrange
//         PrescriptionRequestDto statusUpdateDto = new PrescriptionRequestDto(
//                 1L, 1L, 1L, "500mg", "Twice daily", 10, 20, 2, "Oral", "After meals", PrescriptionStatus.ISSUED
//         );

//         PrescriptionResponseDto issuedResponse = new PrescriptionResponseDto(
//                 1L, 1L, 1L, "John Doe", 1L, "Dr. Smith", 1L, "Aspirin",
//                 "500mg", "Twice daily", 10, 20, 2, "Oral", "After meals", PrescriptionStatus.ISSUED, now
//         );

//         when(prescriptionService.updatePrescription(1L, statusUpdateDto)).thenReturn(issuedResponse);

//         // Act & Assert
//         mockMvc.perform(patch("/api/v1/prescriptions/status/1")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(statusUpdateDto)))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status", is("ISSUED")));

//         verify(prescriptionService, times(1)).updatePrescription(1L, statusUpdateDto);
//     }

//     @Test
//     @DisplayName("Should delete prescription")
//     @WithMockUser(roles = "ADMIN")
//     void testDeletePrescription() throws Exception {
//         // Arrange
//         doNothing().when(prescriptionService).deletePrescription(1L);

//         // Act & Assert
//         mockMvc.perform(delete("/api/v1/prescriptions/1")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isNoContent());

//         verify(prescriptionService, times(1)).deletePrescription(1L);
//     }

//     @Test
//     @DisplayName("Should return 403 when non-ADMIN tries to delete prescription")
//     @WithMockUser(roles = "CLINICIAN")
//     void testDeletePrescriptionForbidden() throws Exception {
//         // Act & Assert
//         mockMvc.perform(delete("/api/v1/prescriptions/1")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isForbidden());
//     }

//     @Test
//     @DisplayName("Should return 401 when unauthenticated user tries to get prescriptions")
//     void testGetPrescriptionsUnauthorized() throws Exception {
//         // Act & Assert
//         mockMvc.perform(get("/api/v1/prescriptions")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isUnauthorized());
//     }

//     @Test
//     @DisplayName("Should return 403 when PATIENT tries to create prescription")
//     @WithMockUser(roles = "PATIENT")
//     void testCreatePrescriptionForbidden() throws Exception {
//         // Act & Assert
//         mockMvc.perform(post("/api/v1/prescriptions")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(prescriptionRequestDto)))
//                 .andExpect(status().isForbidden());
//     }
// }
