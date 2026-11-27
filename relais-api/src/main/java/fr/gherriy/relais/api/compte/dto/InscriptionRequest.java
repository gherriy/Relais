package fr.gherriy.relais.api.compte.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InscriptionRequest(
        @NotBlank(message = "Le nom du projet est obligatoire")
        @Size(max = 100, message = "Le nom du projet ne doit pas dépasser 100 caractères")
        String nomProjet,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "L'email n'est pas valide")
        @Size(max = 255, message = "L'email ne doit pas dépasser 255 caractères")
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 12, max = 72, message = "Le mot de passe doit contenir entre 12 et 72 caractères")
        String motDePasse) {

    @Override
    public String toString() {
        return "InscriptionRequest[nomProjet=" + nomProjet + "]";
    }
}
