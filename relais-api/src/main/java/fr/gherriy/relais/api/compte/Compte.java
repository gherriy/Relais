package fr.gherriy.relais.api.compte;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "compte")
public class Compte {

    @Id
    private UUID id;

    @Column(name = "nom_projet", nullable = false, length = 100)
    private String nomProjet;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "mot_de_passe_hash", nullable = false, length = 100)
    private String motDePasseHash;

    @Column(name = "cree_le", nullable = false)
    private Instant creeLe;

    protected Compte() {

    }
    public Compte(String nomProjet, String email, String motDePasseHash) {
        this.id = UUID.randomUUID();
        this.nomProjet = nomProjet;
        this.email = email;
        this.motDePasseHash = motDePasseHash;
        this.creeLe = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getNomProjet() {
        return nomProjet;
    }

    public String getEmail() {
        return email;
    }

    public String getMotDePasseHash() {
        return motDePasseHash;
    }

    public Instant getCreeLe() {
        return creeLe;
    }
}
