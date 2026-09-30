package com.mediflow.service;

import com.mediflow.model.Patient;
import com.mediflow.repository.PatientDAO;
import java.time.*;
import java.util.*;

public class PatientService {
    private final PatientDAO patientDAO;
    private final Clock clock;

    public PatientService(PatientDAO patientDAO, Clock clock) {
        this.patientDAO = patientDAO;
        this.clock = clock;
    }

    public Patient enregistrer(Patient patient) {
        Map<String, String> erreurs = new LinkedHashMap<>();
        verifierTexte(erreurs, "nom", patient.getNom(), 100);
        verifierTexte(erreurs, "prenom", patient.getPrenom(), 100);
        verifierTexte(erreurs, "numeroSecuriteSociale", patient.getNumeroSecuriteSociale(), 30);
        LocalDate today = LocalDate.now(clock);
        if (patient.getDateNaissance() == null || patient.getDateNaissance().isAfter(today)
                || patient.getDateNaissance().isBefore(today.minusYears(130))) {
            erreurs.put("dateNaissance", "Saisissez une date de naissance valide (130 ans maximum).");
        }
        String tension = patient.getTensionArterielle();
        if (tension == null || !tension.matches("\\d{2,3}/\\d{2,3}")) {
            erreurs.put("tensionArterielle", "Format attendu : 120/80, en mmHg.");
        } else {
            String[] valeurs = tension.split("/");
            int systolique = Integer.parseInt(valeurs[0]);
            int diastolique = Integer.parseInt(valeurs[1]);
            if (systolique < 40 || systolique > 300 || diastolique < 20 || diastolique > 200 || systolique <= diastolique) {
                erreurs.put("tensionArterielle", "Vérifiez la tension : systolique 40–300, diastolique 20–200, systolique supérieure.");
            }
        }
        if (patient.getFrequenceCardiaque() < 20 || patient.getFrequenceCardiaque() > 250)
            erreurs.put("frequenceCardiaque", "Valeur attendue : 20 à 250 battements/min.");
        if (!Double.isFinite(patient.getTemperature()) || patient.getTemperature() < 30 || patient.getTemperature() > 45)
            erreurs.put("temperature", "Valeur attendue : 30 à 45 °C.");
        if (patient.getFrequenceRespiratoire() < 5 || patient.getFrequenceRespiratoire() > 80)
            erreurs.put("frequenceRespiratoire", "Valeur attendue : 5 à 80 cycles/min.");
        if (!erreurs.isEmpty()) throw new ValidationException(erreurs);
        patient.setNom(patient.getNom().trim());
        patient.setPrenom(patient.getPrenom().trim());
        patient.setNumeroSecuriteSociale(patient.getNumeroSecuriteSociale().trim());
        patient.setId(null);
        patient.setConsultation(null);
        patient.setDateArrivee(LocalDateTime.now(clock));
        return patientDAO.save(patient);
    }

    public List<Patient> patientsDuJour() {
        LocalDate today = LocalDate.now(clock);
        return patientDAO.findAll().stream()
                .filter(p -> p.getDateArrivee().toLocalDate().equals(today))
                .sorted(Comparator.comparing(Patient::getDateArrivee).thenComparing(Patient::getId))
                .toList();
    }

    public List<Patient> patientsEnAttente() {
        return patientsDuJour().stream().filter(p -> p.getConsultation() == null).toList();
    }

    private void verifierTexte(Map<String, String> erreurs, String champ, String valeur, int max) {
        if (valeur == null || valeur.isBlank() || valeur.trim().length() > max)
            erreurs.put(champ, "Ce champ est obligatoire (" + max + " caractères maximum).");
    }
}
