package com.mediflow.model;

public class Utilisateur 
{
    private long    id;
    private String  nom;
    private String  email;
    private String  motDePasseHash;
    private Role    role;

    public Utilisateur(long id, String nom, String email, String motDePasseHash, Role role) 
    {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.motDePasseHash = motDePasseHash;
        this.role = role;
    }

    public long getId() 
    { 
        return id;
    }
    public String getNom() 
    { 
        return nom; 
    }
    public String getEmail() 
    { 
        return email; 
    }
    public String getMotDePasseHash() 
    { 
        return motDePasseHash; 
    }
    public Role getRole() 
    { 
        return role; 
    }
}
