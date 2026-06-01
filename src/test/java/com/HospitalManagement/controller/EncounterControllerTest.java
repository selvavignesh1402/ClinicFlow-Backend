// package com.HospitalManagement.controller;

// import com.HospitalManagement.enums.EncounterStatus;
// import com.HospitalManagement.requestdto.EncounterRequestDto;
// import com.HospitalManagement.responsedto.EncounterResponseDto;
// import com.HospitalManagement.service.EncounterService;
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
// @DisplayName("Encounter Controller Tests")
// class EncounterControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private EncounterService encounterService;

//     @Autowired
//     private ObjectMapper objectMapper;

//     private EncounterRequestDto encounterRequestDto;
//     private EncounterResponseDto encounterResponseDto;
//     private LocalDateTime now;

//     @BeforeEach
//     void setUp() {
//         now = LocalDateTime.now();

//         encounterRequestDto = new EncounterRequestDto(
//                 1L,  // patientId
//                 "Routine Checkup",  // visitType
//                 "Patient complaining of headache",  // chiefComplaint
//                 "{\"temp\": 37.5}",  // vitalsJson
//                 "{\"notes\": \"Patient seems fine\"}",  // notesJson
//                 "{\"diagnosis\": \"Common Cold\"}",  // diagnosesJson
//                 "{\"orders\": []}",  // ordersJson
//                 "{\"prescriptions\": []}",  // prescriptionsJson
//                 now,  // startAt
//                 EncounterStatus.IN_PROGRESS  // status
//         );

//         encounterResponseDto = new EncounterResponseDto(
//                 1L,  // encounterId
//                 1L,  // patientId
//                 "John Doe",  // patientName
//                 1L,  // clinicianId
//                 "Dr. Smith",  // clinicianName
//                 "Routine Checkup",  // visitType
//                 "Patient complaining of headache",  // chiefComplaint
//                 "{\"temp\": 37.5}",  // vitalsJson
//                 "{\"notes\": \"Patient seems fine\"}",  // notesJson
//                 "{\"diagnosis\": \"Common Cold\"}",  // diagnosesJson
//                 "{\"orders\": []}",  // ordersJson
//                 "{\"prescriptions\": []}",  // prescriptionsJson
//                 now,  // startAt
//                 null,  // endAt
//                 EncounterStatus.IN_PROGRESS,  // status
//                 null,  // signedById
//                 null,  // signedByName
//                 null  // signedAt
//         );
//     }

//     @Test
//     @DisplayName("Should get all encounters")
//     void testGetAllEncounters() throws Exception {
//         // Arrange
//         List<EncounterResponseDto> encounters = Arrays.asList(encounterResponseDto);
//         when(encounterService.getAllEncounters()).thenReturn(encounters);

//         // Act & Assert
//         mockMvc.perform(get("/api/v1/clinician/encounters")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$", hasSize(1)))
//                 .andExpect(jsonPath("$[0].encounterId", is(1)))
//                 .andExpect(jsonPath("$[0].visitType", is("Routine Checkup")))
//                 .andExpect(jsonPath("$[0].status", is("IN_PROGRESS")));

//         verify(encounterService, times(1)).getAllEncounters();
//     }

//     @Test
//     @DisplayName("Should get encounter by ID")
//     void testGetEncounterById() throws Exception {
//         // Arrange
//         when(encounterService.getEncounterById(1L)).thenReturn(encounterResponseDto);

//         // Act & Assert
//         mockMvc.perform(get("/api/v1/clinician/encounters/1")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.encounterId", is(1)))
//                 .andExpect(jsonPath("$.patientName", is("John Doe")))
//                 .andExpect(jsonPath("$.chiefComplaint", is("Patient complaining of headache")))
//                 .andExpect(jsonPath("$.visitType", is("Routine Checkup")));

//         verify(encounterService, times(1)).getEncounterById(1L);
//     }

//     @Test
//     @DisplayName("Should create encounter successfully")
//     @WithMockUser(roles = "CLINICIAN")
//     void testCreateEncounter() throws Exception {
//         // Arrange
//         when(encounterService.createEncounter(any(EncounterRequestDto.class)))
//                 .thenReturn(encounterResponseDto);

//         // Act & Assert
//         mockMvc.perform(post("/api/v1/clinician/encounters")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(encounterRequestDto)))
//                 .andExpect(status().isCreated())
//                 .andExpect(jsonPath("$.encounterId", is(1)))
//                 .andExpect(jsonPath("$.status", is("IN_PROGRESS")))
//                 .andExpect(jsonPath("$.clinicianName", is("Dr. Smith")));

//         verify(encounterService, times(1)).createEncounter(any(EncounterRequestDto.class));
//     }

//     @Test
//     @DisplayName("Should update encounter successfully")
//     @WithMockUser(roles = "CLINICIAN")
//     void testUpdateEncounter() throws Exception {
//         // Arrange
//         EncounterRequestDto updateDto = new EncounterRequestDto(
//                 1L, "Follow-up Visit", "Patient recovery check",
//                 "{\"temp\": 37.0}", "{\"notes\": \"Patient recovering well\"}",
//                 "{\"diagnosis\": \"Common Cold - Recovering\"}", "{\"orders\": []}", "{\"prescriptions\": []}",
//                 now, EncounterStatus.IN_PROGRESS
//         );

//         EncounterResponseDto updatedResponse = new EncounterResponseDto(
//                 1L, 1L, "John Doe", 1L, "Dr. Smith", "Follow-up Visit",
//                 "Patient recovery check", "{\"temp\": 37.0}", "{\"notes\": \"Patient recovering well\"}",
//                 "{\"diagnosis\": \"Common Cold - Recovering\"}", "{\"orders\": []}", "{\"prescriptions\": []}",
//                 now, null, EncounterStatus.IN_PROGRESS, null, null, null
//         );

//         when(encounterService.updateEncounter(1L, updateDto)).thenReturn(updatedResponse);

//         // Act & Assert
//         mockMvc.perform(put("/api/v1/clinician/encounters/1")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(updateDto)))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.visitType", is("Follow-up Visit")))
//                 .andExpect(jsonPath("$.chiefComplaint", is("Patient recovery check")));

//         verify(encounterService, times(1)).updateEncounter(1L, updateDto);
//     }

//     @Test
//     @DisplayName("Should complete encounter successfully")
//     @WithMockUser(roles = "CLINICIAN")
//     void testCompleteEncounter() throws Exception {
//         // Arrange
//         EncounterRequestDto completeDto = new EncounterRequestDto(
//                 1L, "Routine Checkup", "Patient complaining of headache",
//                 "{\"temp\": 37.5}", "{\"notes\": \"Patient seems fine\"}",
//                 "{\"diagnosis\": \"Common Cold\"}", "{\"orders\": []}", "{\"prescriptions\": []}",
//                 now, EncounterStatus.COMPLETED
//         );

//         LocalDateTime endTime = now.plusHours(1);
//         EncounterResponseDto completedResponse = new EncounterResponseDto(
//                 1L, 1L, "John Doe", 1L, "Dr. Smith", "Routine Checkup",
//                 "Patient complaining of headache", "{\"temp\": 37.5}", "{\"notes\": \"Patient seems fine\"}",
//                 "{\"diagnosis\": \"Common Cold\"}", "{\"orders\": []}", "{\"prescriptions\": []}",
//                 now, endTime, EncounterStatus.COMPLETED, 1L, "Dr. Smith", endTime
//         );

//         when(encounterService.updateEncounter(1L, completeDto)).thenReturn(completedResponse);

//         // Act & Assert
//         mockMvc.perform(put("/api/v1/clinician/encounters/1")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(completeDto)))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status", is("COMPLETED")))
//                 .andExpect(jsonPath("$.signedByName", is("Dr. Smith")));

//         verify(encounterService, times(1)).updateEncounter(1L, completeDto);
//     }

//     @Test
//     @DisplayName("Should delete encounter")
//     @WithMockUser(roles = "CLINICIAN")
//     void testDeleteEncounter() throws Exception {
//         // Arrange
//         doNothing().when(encounterService).deleteEncounter(1L);

//         // Act & Assert
//         mockMvc.perform(delete("/api/v1/clinician/encounters/1")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isNoContent());

//         verify(encounterService, times(1)).deleteEncounter(1L);
//     }

//     @Test
//     @DisplayName("Should return 404 when encounter not found")
//     void testGetEncounterNotFound() throws Exception {
//         // Arrange
//         when(encounterService.getEncounterById(999L))
//                 .thenThrow(new RuntimeException("Encounter not found"));

//         // Act & Assert
//         mockMvc.perform(get("/api/v1/clinician/encounters/999")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isInternalServerError());

//         verify(encounterService, times(1)).getEncounterById(999L);
//     }

//     @Test
//     @DisplayName("Should handle invalid encounter request with bad data")
//     @WithMockUser(roles = "CLINICIAN")
//     void testCreateEncounterWithInvalidData() throws Exception {
//         // Arrange
//         String invalidJson = "{\"patientId\": -1}";  // Invalid patient ID

//         // Act & Assert
//         mockMvc.perform(post("/api/v1/clinician/encounters")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(invalidJson))
//                 .andExpect(status().isBadRequest());
//     }

//     @Test
//     @DisplayName("Should return encounters with vitals and notes as JSON")
//     void testGetEncounterWithJsonData() throws Exception {
//         // Arrange
//         when(encounterService.getEncounterById(1L)).thenReturn(encounterResponseDto);

//         // Act & Assert
//         mockMvc.perform(get("/api/v1/clinician/encounters/1")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.vitalsJson", notNullValue()))
//                 .andExpect(jsonPath("$.notesJson", notNullValue()))
//                 .andExpect(jsonPath("$.diagnosesJson", notNullValue()));

//         verify(encounterService, times(1)).getEncounterById(1L);
//     }
// }
