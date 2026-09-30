package com.mediflow.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Modèle prévu pour la prochaine étape : saisie et clôture. */
public class Consultation {
    private final long id;
    private final long patientId;
    private final long medecinId;
    private final String motif;
    private final String observations;
    private final String diagnostic;
    private final String traitement;
    private final BigDecimal cout;
    private final String statut;
    private final LocalDateTime dateConsultation;

    public Consultation(long id, long patientId, long medecinId, String motif, String observations,
                        String diagnostic, String traitement, BigDecimal cout, String statut,
                        LocalDateTime dateConsultation) {
        this.id = id;
        this.patientId = patientId;
        this.medecinId = medecinId;
        this.motif = motif;
        this.observations = observations;
        this.diagnostic = diagnostic;
        this.traitement = traitement;
        this.cout = cout;
        this.statut = statut;
        this.dateConsultation = dateConsultation;
    }

    public long getId() { return id; }
    public long getPatientId() { return patientId; }
    public long getMedecinId() { return medecinId; }
    public String getMotif() { return motif; }
    public String getObservations() { return observations; }
    public String getDiagnostic() { return diagnostic; }
    public String getTraitement() { return traitement; }
    public BigDecimal getCout() { return cout; }
    public String getStatut() { return statut; }
    public LocalDateTime getDateConsultation() { return dateConsultation; }
}
