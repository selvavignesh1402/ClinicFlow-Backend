package com.HospitalManagement.service;

import com.HospitalManagement.entity.Appointment;
import com.HospitalManagement.entity.Patient;
import com.HospitalManagement.entity.User;
import com.HospitalManagement.enums.AppointmentStatus;
import com.HospitalManagement.repository.AppointmentRepository;
import com.HospitalManagement.repository.PatientRepository;
import com.HospitalManagement.repository.UserRepository;
import com.HospitalManagement.requestdto.AppointmentRequestDto;
import com.HospitalManagement.responsedto.AppointmentResponseDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);
    private static final List<AppointmentStatus> NON_BLOCKING_STATUSES = List.of(AppointmentStatus.CANCELLED, AppointmentStatus.NO_SHOW);

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public List<AppointmentResponseDto> getAllAppointments() {
        logger.debug("Fetching all appointments");
        List<AppointmentResponseDto> appointments = appointmentRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
        logger.info("Retrieved {} appointments", appointments.size());
        return appointments;
    }

    public AppointmentResponseDto getAppointmentById(Long id) {
        logger.debug("Fetching appointment with ID: {}", id);
        AppointmentResponseDto appointment = toResponseDto(findAppointment(id));
        logger.info("Retrieved appointment - ID: {}, PatientID: {}", id, appointment.patientId());
        return appointment;
    }

    public List<AppointmentResponseDto> getAppointmentsByPatient(Long patientId) {
        logger.debug("Fetching appointments for patient ID: {}", patientId);
        List<AppointmentResponseDto> appointments = appointmentRepository.findByPatientPatientIdOrderByStartAtDesc(patientId)
                .stream()
                .map(this::toResponseDto)
                .toList();
        logger.info("Retrieved {} appointments for patient ID: {}", appointments.size(), patientId);
        return appointments;
    }

    public List<AppointmentResponseDto> getClinicianAppointments(Long clinicianId, LocalDateTime from, LocalDateTime to) {
        if (!from.isBefore(to)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "from must be before to");
        }

        return appointmentRepository.findByClinicianUserIdAndStartAtBetweenOrderByStartAtAsc(clinicianId, from, to)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Transactional
    public AppointmentResponseDto createAppointment(AppointmentRequestDto request) {
        logger.info("Creating new appointment - PatientID: {}, ClinicianID: {}, Department: {}", 
            request.patientId(), request.clinicianId(), request.department());
        validateTimeRange(request.startAt(), request.endAt());
        ensureClinicianAvailable(request.clinicianId(), request.startAt(), request.endAt(), null);

        Appointment appointment = new Appointment();
        mapRequestToEntity(request, appointment);
        AppointmentStatus status = request.status() != null ? request.status() : AppointmentStatus.SCHEDULED;
        appointment.setStatus(status);

        Appointment savedAppointment = appointmentRepository.save(appointment);
        logger.info("Appointment created successfully - AppointmentID: {}, PatientID: {}, Status: {}", 
            savedAppointment.getApptId(), request.patientId(), status);
        return toResponseDto(savedAppointment);
    }

    @Transactional
    public AppointmentResponseDto updateAppointment(Long id, AppointmentRequestDto request) {
        logger.info("Updating appointment - AppointmentID: {}, NewStatus: {}", id, request.status());
        validateTimeRange(request.startAt(), request.endAt());

        Appointment appointment = findAppointment(id);
        boolean slotChanged = !appointment.getClinician().getUserId().equals(request.clinicianId())
                || !appointment.getStartAt().equals(request.startAt())
                || !appointment.getEndAt().equals(request.endAt());

        if (slotChanged) {
            ensureClinicianAvailable(request.clinicianId(), request.startAt(), request.endAt(), id);
        }

        mapRequestToEntity(request, appointment);
        if (request.status() != null) {
            appointment.setStatus(request.status());
        }

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        logger.info("Appointment updated successfully - AppointmentID: {}, Status: {}", id, appointment.getStatus());
        return toResponseDto(updatedAppointment);
    }

    @Transactional
    public AppointmentResponseDto checkInAppointment(Long id) {
        logger.info("Checking in appointment - AppointmentID: {}", id);
        Appointment appointment = findAppointment(id);
        appointment.setStatus(AppointmentStatus.CHECKED_IN);
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        logger.info("Appointment checked in successfully - AppointmentID: {}", id);
        return toResponseDto(updatedAppointment);
    }

    @Transactional
    public AppointmentResponseDto completeAppointment(Long id) {
        logger.info("Completing appointment - AppointmentID: {}", id);
        Appointment appointment = findAppointment(id);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        logger.info("Appointment completed successfully - AppointmentID: {}", id);
        return toResponseDto(updatedAppointment);
    }

    @Transactional
    public AppointmentResponseDto cancelAppointment(Long id) {
        logger.info("Cancelling appointment - AppointmentID: {}", id);
        Appointment appointment = findAppointment(id);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        logger.info("Appointment cancelled successfully - AppointmentID: {}", id);
        return toResponseDto(updatedAppointment);
    }

    private void mapRequestToEntity(AppointmentRequestDto request, Appointment appointment) {
        appointment.setPatient(findPatient(request.patientId()));
        appointment.setClinician(findUser(request.clinicianId()));
        appointment.setDepartment(request.department().trim());
        appointment.setServiceType(request.serviceType().trim());
        appointment.setStartAt(request.startAt());
        appointment.setEndAt(request.endAt());
        appointment.setCreatedBy(findUser(request.createdById()));
    }

    private void validateTimeRange(LocalDateTime startAt, LocalDateTime endAt) {
        if (!startAt.isBefore(endAt)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment start time must be before end time");
        }
    }

    private void ensureClinicianAvailable(Long clinicianId, LocalDateTime startAt, LocalDateTime endAt, Long currentAppointmentId) {
        boolean overlaps = currentAppointmentId == null
                ? appointmentRepository.existsByClinicianUserId(
                clinicianId,
                endAt,
                startAt,
                NON_BLOCKING_STATUSES
        )
                : appointmentRepository.existsByClinicianUserIdAndStartAtLessThanAndEndAtGreaterThanAndStatusNotInAndApptIdNot(
                clinicianId,
                endAt,
                startAt,
                NON_BLOCKING_STATUSES,
                currentAppointmentId
        );

        if (overlaps) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Clinician already has an appointment in this slot");
        }
    }

    private Appointment findAppointment(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found with id: " + id));
    }

    private Patient findPatient(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found with id: " + id));
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
    }

    private AppointmentResponseDto toResponseDto(Appointment appointment) {
        return new AppointmentResponseDto(
                appointment.getApptId(),
                appointment.getPatient().getPatientId(),
                appointment.getPatient().getMrn(),
                appointment.getPatient().getName(),
                appointment.getClinician().getUserId(),
                appointment.getClinician().getName(),
                appointment.getDepartment(),
                appointment.getServiceType(),
                appointment.getStartAt(),
                appointment.getEndAt(),
                appointment.getStatus(),
                appointment.getCreatedBy().getUserId(),
                appointment.getCreatedBy().getName(),
                appointment.getCreatedAt()
        );
    }
}