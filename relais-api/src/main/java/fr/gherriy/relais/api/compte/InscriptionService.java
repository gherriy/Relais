package fr.gherriy.relais.api.compte;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.gherriy.relais.api.cleapi.CleApi;
import fr.gherriy.relais.api.cleapi.CleApiRepository;
import fr.gherriy.relais.api.cleapi.CleGeneree;
import fr.gherriy.relais.api.cleapi.Environnement;
import fr.gherriy.relais.api.cleapi.GenerateurCleApi;
import fr.gherriy.relais.api.compte.dto.InscriptionRequest;
import fr.gherriy.relais.api.compte.dto.InscriptionResponse;

@Service
@Transactional(readOnly = true)
public class InscriptionService {

    private static final Logger log = LoggerFactory.getLogger(InscriptionService.class);
    private static final String NOM_PREMIERE_CLE = "Clé de test";

    private final CompteRepository compteRepository;
    private final CleApiRepository cleApiRepository;
    private final GenerateurCleApi generateurCleApi;
    private final PasswordEncoder passwordEncoder;

    public InscriptionService(CompteRepository compteRepository, CleApiRepository cleApiRepository,
            GenerateurCleApi generateurCleApi, PasswordEncoder passwordEncoder) {
        this.compteRepository = compteRepository;
        this.cleApiRepository = cleApiRepository;
        this.generateurCleApi = generateurCleApi;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public InscriptionResponse inscrire(InscriptionRequest demande) {
        String email = demande.email().trim().toLowerCase(Locale.ROOT);
        if (compteRepository.existsByEmail(email)) {
            throw new EmailDejaUtiliseException();
        }

        Compte compte = enregistrerCompte(demande.nomProjet().trim(), email, demande.motDePasse());

        CleGeneree cle = generateurCleApi.generer(Environnement.TEST);
        cleApiRepository.save(new CleApi(compte, NOM_PREMIERE_CLE, Environnement.TEST, cle.prefixe(), cle.hash()));

        log.info("Compte créé : compteId={}, premiereCle={}", compte.getId(), cle.prefixe());
        return new InscriptionResponse(compte.getId(), compte.getNomProjet(), compte.getEmail(), cle.valeur());
    }

    private Compte enregistrerCompte(String nomProjet, String email, String motDePasse) {
        try {
            return compteRepository.saveAndFlush(new Compte(nomProjet, email, passwordEncoder.encode(motDePasse)));
        } catch (DataIntegrityViolationException e) {
            throw new EmailDejaUtiliseException(e);
        }
    }
}
