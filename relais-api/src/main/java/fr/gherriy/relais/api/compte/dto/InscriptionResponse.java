package fr.gherriy.relais.api.compte.dto;

import java.util.UUID;

public record InscriptionResponse(UUID compteId, String nomProjet, String email, String cleApi) {
    @Override 
    public String toString() {
        return "InscriptionResponse[compteId=" + compteId + "]";
    }
}
