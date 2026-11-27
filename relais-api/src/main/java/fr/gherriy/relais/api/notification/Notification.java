package fr.gherriy.relais.api.notification;

import java.time.Instant;
import java.util.UUID;

import fr.gherriy.relais.api.cleapi.CleApi;
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
@Table(name = "notification")
public class Notification {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "compte_id", nullable = false)
    private Compte compte;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cle_api_id", nullable = false)
    private CleApi cleApi;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Canal canal;

    @Column(nullable = false, length = 320)
    private String destinataire;

    @Column(length = 255)
    private String sujet;

    @Column(nullable = false, columnDefinition = "text")
    private String contenu;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutNotification statut;

    @Column(name = "cree_le", nullable = false)
    private Instant creeLe;

    @Column(name = "mis_a_jour_le", nullable = false)
    private Instant misAJourLe;

    protected Notification() {
        // requis par JPA
    }

    public UUID getId() {
        return id;
    }

    public Compte getCompte() {
        return compte;
    }

    public CleApi getCleApi() {
        return cleApi;
    }

    public Canal getCanal() {
        return canal;
    }

    public String getDestinataire() {
        return destinataire;
    }

    public String getSujet() {
        return sujet;
    }

    public String getContenu() {
        return contenu;
    }

    public StatutNotification getStatut() {
        return statut;
    }

    public Instant getCreeLe() {
        return creeLe;
    }

    public Instant getMisAJourLe() {
        return misAJourLe;
    }
}
