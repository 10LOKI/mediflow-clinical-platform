package com.mediflow.model;

public class Utilisateur {
    private final long id;
    private final String nom;
    private final String email;
    private final String motDePasseHash;
    private final Role role;

    public Utilisateur(long id, String nom, String email, String motDePasseHash, Role role) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.motDePasseHash = motDePasseHash;
        this.role = role;
    }

    public long getId() { return id; }
    public String getNom() { return nom; }
    public String getEmail() { return email; }
    public String getMotDePasseHash() { return motDePasseHash; }
    public Role getRole() { return role; }
}
