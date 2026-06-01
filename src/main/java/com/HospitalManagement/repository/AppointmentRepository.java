package com.HospitalManagement.repository;

import com.HospitalManagement.entity.Appointment;
import com.HospitalManagement.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByClinicianUserId(
            Long clinicianId,
            LocalDateTime endAt,
            LocalDateTime startAt,
            List<AppointmentStatus> excludedStatuses
    );

    boolean existsByClinicianUserIdAndStartAtLessThanAndEndAtGreaterThanAndStatusNotInAndApptIdNot(
            Long clinicianId,
            LocalDateTime endAt,
            LocalDateTime startAt,
            List<AppointmentStatus> excludedStatuses,
            Long apptId
    );

    List<Appointment> findByPatientPatientIdOrderByStartAtDesc(Long patientId);

    List<Appointment> findByClinicianUserIdAndStartAtBetweenOrderByStartAtAsc(
            Long clinicianId,
            LocalDateTime from,
            LocalDateTime to
    );
}