package com.mediflow.repository;

import com.mediflow.model.Consultation;
import java.util.Optional;

/** Lecture uniquement à ce stade du livrable. */
public interface ConsultationDAO {
    Optional<Consultation> findByPatientId(long patientId);
}
