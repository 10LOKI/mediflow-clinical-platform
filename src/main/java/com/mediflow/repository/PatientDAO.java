package com.mediflow.repository;

import com.mediflow.model.Patient;
import java.util.List;
import java.util.Optional;

public interface PatientDAO {
    Patient save(Patient patient);
    List<Patient> findAll();
    Optional<Patient> findById(long id);
}
