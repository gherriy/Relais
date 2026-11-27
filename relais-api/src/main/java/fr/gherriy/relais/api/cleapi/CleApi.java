package fr.gherriy.relais.api.cleapi;

import java.time.Instant;
import java.util.UUID;

import fr.gherriy.relais.api.compte.Compte;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cle_api")
public class CleApi {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "compte_id", nullable = false)
    private Compte compte;

    @Column(nullable = false, length = 100)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Environnement environnement;

    @Column(nullable = false, length = 20)
    private String prefixe;

    @Column(nullable = false, unique = true, length = 64)
    private String hash;

    @Column(name = "cree_le", nullable = false)
    private Instant creeLe;

    @Column(name = "revoquee_le")
    private Instant revoqueeLe;

    protected CleApi() {
    }

    public CleApi(Compte compte, String nom, Environnement environnement, String prefixe, String hash) {
        this.id = UUID.randomUUID();
        this.compte = compte;
        this.nom = nom;
        this.environnement = environnement;
        this.prefixe = prefixe;
        this.hash = hash;
        this.creeLe = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public Compte getCompte() {
        return compte;
    }

    public String getNom() {
        return nom;
    }

    public Environnement getEnvironnement() {
        return environnement;
    }

    public String getPrefixe() {
        return prefixe;
    }

    public String getHash() {
        return hash;
    }

    public Instant getCreeLe() {
        return creeLe;
    }

    public Instant getRevoqueeLe() {
        return revoqueeLe;
    }
}
