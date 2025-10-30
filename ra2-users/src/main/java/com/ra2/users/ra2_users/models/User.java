package com.ra2.users.ra2_users.models;

import java.sql.Timestamp;

public class User {
    private Long id;
    private String nom;
    private String description;
    private String email;
    private String password;
    private Timestamp ultimAcces;
    private Timestamp dataCreated;
    private Timestamp dataUpdated;

    public User() {}

    public User(Long id, String nom, String description, String email, String password,
                Timestamp ultimAcces, Timestamp dataCreated, Timestamp dataUpdated) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.email = email;
        this.password = password;
        this.ultimAcces = ultimAcces;
        this.dataCreated = dataCreated;
        this.dataUpdated = dataUpdated;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Timestamp getUltimAcces() { return ultimAcces; }
    public void setUltimAcces(Timestamp ultimAcces) { this.ultimAcces = ultimAcces; }

    public Timestamp getDataCreated() { return dataCreated; }
    public void setDataCreated(Timestamp dataCreated) { this.dataCreated = dataCreated; }

    public Timestamp getDataUpdated() { return dataUpdated; }
    public void setDataUpdated(Timestamp dataUpdated) { this.dataUpdated = dataUpdated; }

    @Override
    public String toString() {
        return "User [id=" + id + ", nom=" + nom + ", description=" + description + ", email=" + email
                + ", password=" + password + ", ultimAcces=" + ultimAcces
                + ", dataCreated=" + dataCreated + ", dataUpdated=" + dataUpdated + "]";
    }
}
