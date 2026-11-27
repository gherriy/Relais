package fr.gherriy.relais.api.compte;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.gherriy.relais.api.compte.dto.InscriptionRequest;
import fr.gherriy.relais.api.compte.dto.InscriptionResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class InscriptionController {

    private final InscriptionService inscriptionService;

    public InscriptionController(InscriptionService inscriptionService) {
        this.inscriptionService = inscriptionService;
    }

    @PostMapping("/inscription")
    @ResponseStatus(HttpStatus.CREATED)
    public InscriptionResponse inscrire(@Valid @RequestBody InscriptionRequest demande) {
        return inscriptionService.inscrire(demande);
    }
}
