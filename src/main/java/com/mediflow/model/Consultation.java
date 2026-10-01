package com.mediflow.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Consultation 
{
    private long            id;
    private long            patientId;
    private long            medecinId;
    private String          motif;
    private String          observations;
    private String          diagnostic;
    private String          traitement;
    private BigDecimal      cout;
    private String          statut;
    private LocalDateTime   dateConsultation;

    public Consultation(long id, long patientId, long medecinId, String motif, String observations, String diagnostic, String traitement, BigDecimal cout, String statut, LocalDateTime dateConsultation) 
    {
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

    public long getId() 
    { 
        return id; 
    }
    public long getPatientId() 
    { 
        return patientId; 
    }
    public long getMedecinId() 
    { 
        return medecinId; 
    }
    public String getMotif() 
    { 
        return motif; 
    }
    public String getObservations() 
    { 
        return observations; 
    }
    public String getDiagnostic() 
    { 
        return diagnostic; 
    }
    public String getTraitement() 
    { 
        return traitement; 
    }
    public BigDecimal getCout() 
    { 
        return cout; 
    }
    public String getStatut() 
    { 
        return statut; 
    }
    public LocalDateTime getDateConsultation() 
    { 
        return dateConsultation; 
    }
}
