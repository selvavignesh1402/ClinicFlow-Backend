package com.HospitalManagement.repository;

import com.HospitalManagement.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByMrn(String mrn);

    boolean existsByMrn(String mrn);

    Optional<Patient> findFirstByNameIgnoreCaseAndDobAndPrimaryContact(
            String name,
            LocalDate dob,
            String primaryContact
    );
}