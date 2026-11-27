package fr.gherriy.relais.api.notification;

import java.time.Instant;
import java.util.UUID;

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
@Table(name = "tentative")
public class Tentative {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    @Column(nullable = false)
    private short numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private StatutTentative statut;

    @Column(name = "message_erreur", columnDefinition = "text")
    private String messageErreur;

    @Column(name = "tentee_le", nullable = false)
    private Instant tenteeLe;

    protected Tentative() {
    }

    public UUID getId() {
        return id;
    }

    public Notification getNotification() {
        return notification;
    }

    public short getNumero() {
        return numero;
    }

    public StatutTentative getStatut() {
        return statut;
    }

    public String getMessageErreur() {
        return messageErreur;
    }

    public Instant getTenteeLe() {
        return tenteeLe;
    }
}
