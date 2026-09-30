package com.mediflow.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Patient {
    private Long id;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String numeroSecuriteSociale;
    private String tensionArterielle;
    private int frequenceCardiaque;
    private double temperature;
    private int frequenceRespiratoire;
    private LocalDateTime dateArrivee;
    private Consultation consultation;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public String getNumeroSecuriteSociale() { return numeroSecuriteSociale; }
    public void setNumeroSecuriteSociale(String numeroSecuriteSociale) { this.numeroSecuriteSociale = numeroSecuriteSociale; }
    public String getTensionArterielle() { return tensionArterielle; }
    public void setTensionArterielle(String tensionArterielle) { this.tensionArterielle = tensionArterielle; }
    public int getFrequenceCardiaque() { return frequenceCardiaque; }
    public void setFrequenceCardiaque(int frequenceCardiaque) { this.frequenceCardiaque = frequenceCardiaque; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public int getFrequenceRespiratoire() { return frequenceRespiratoire; }
    public void setFrequenceRespiratoire(int frequenceRespiratoire) { this.frequenceRespiratoire = frequenceRespiratoire; }
    public LocalDateTime getDateArrivee() { return dateArrivee; }
    public void setDateArrivee(LocalDateTime dateArrivee) { this.dateArrivee = dateArrivee; }
    public Consultation getConsultation() { return consultation; }
    public void setConsultation(Consultation consultation) { this.consultation = consultation; }
}
