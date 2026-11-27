package fr.gherriy.relais.api.compte;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import fr.gherriy.relais.api.cleapi.CleApi;
import fr.gherriy.relais.api.cleapi.CleApiRepository;
import fr.gherriy.relais.api.cleapi.CleGeneree;
import fr.gherriy.relais.api.cleapi.Environnement;
import fr.gherriy.relais.api.cleapi.GenerateurCleApi;
import fr.gherriy.relais.api.compte.dto.InscriptionRequest;
import fr.gherriy.relais.api.compte.dto.InscriptionResponse;

@ExtendWith(MockitoExtension.class)
class InscriptionServiceTest {

    private static final CleGeneree CLE = new CleGeneree("rl_test_abcdefSECRET", "rl_test_abcdef", "hash-sha256");

    @Mock
    private CompteRepository compteRepository;

    @Mock
    private CleApiRepository cleApiRepository;

    @Mock
    private GenerateurCleApi generateurCleApi;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private InscriptionService service;

    private final InscriptionRequest demande =
            new InscriptionRequest("Mon projet", "dev@exemple.fr", "motdepasse-solide");

    @Test
    void doitCreerLeCompteEtRenvoyerLaCleComplete() {
        preparerUneInscriptionReussie();

        InscriptionResponse reponse = service.inscrire(demande);

        assertThat(reponse.compteId()).isNotNull();
        assertThat(reponse.nomProjet()).isEqualTo("Mon projet");
        assertThat(reponse.email()).isEqualTo("dev@exemple.fr");
        assertThat(reponse.cleApi()).isEqualTo("rl_test_abcdefSECRET");
    }

    @Test
    void doitStockerLeHashDuMotDePasseEtJamaisLeMotDePasse() {
        preparerUneInscriptionReussie();

        service.inscrire(demande);

        ArgumentCaptor<Compte> compte = ArgumentCaptor.forClass(Compte.class);
        verify(compteRepository).saveAndFlush(compte.capture());
        assertThat(compte.getValue().getMotDePasseHash()).isEqualTo("hash-bcrypt");
    }

    @Test
    void doitEnregistrerUneCleDeTestAvecSeulementSonPrefixeEtSonHash() {
        preparerUneInscriptionReussie();

        InscriptionResponse reponse = service.inscrire(demande);

        ArgumentCaptor<CleApi> cle = ArgumentCaptor.forClass(CleApi.class);
        verify(cleApiRepository).save(cle.capture());
        assertThat(cle.getValue().getEnvironnement()).isEqualTo(Environnement.TEST);
        assertThat(cle.getValue().getPrefixe()).isEqualTo("rl_test_abcdef");
        assertThat(cle.getValue().getHash()).isEqualTo("hash-sha256");
        assertThat(cle.getValue().getCompte().getId()).isEqualTo(reponse.compteId());
    }

    @Test
    void doitNormaliserLEmailAvantDeVerifierSonUnicite() {
        preparerUneInscriptionReussie();
        InscriptionRequest emailEnDesordre =
                new InscriptionRequest("Mon projet", "  Dev@Exemple.FR ", "motdepasse-solide");

        InscriptionResponse reponse = service.inscrire(emailEnDesordre);

        verify(compteRepository).existsByEmail("dev@exemple.fr");
        assertThat(reponse.email()).isEqualTo("dev@exemple.fr");
    }

    @Test
    void doitRefuserUnEmailDejaUtilise() {
        when(compteRepository.existsByEmail("dev@exemple.fr")).thenReturn(true);

        assertThatThrownBy(() -> service.inscrire(demande))
                .isInstanceOf(EmailDejaUtiliseException.class);

        verify(compteRepository, never()).saveAndFlush(any());
        verifyNoInteractions(cleApiRepository, generateurCleApi);
    }

    @Test
    void doitRefuserUnEmailEnregistreAuMemeMomentParUneAutreInscription() {
        when(compteRepository.existsByEmail("dev@exemple.fr")).thenReturn(false);
        when(passwordEncoder.encode("motdepasse-solide")).thenReturn("hash-bcrypt");
        when(compteRepository.saveAndFlush(any(Compte.class)))
                .thenThrow(new DataIntegrityViolationException("uk_compte_email"));

        assertThatThrownBy(() -> service.inscrire(demande))
                .isInstanceOf(EmailDejaUtiliseException.class);

        verifyNoInteractions(cleApiRepository);
    }

    private void preparerUneInscriptionReussie() {
        when(compteRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hash-bcrypt");
        when(compteRepository.saveAndFlush(any(Compte.class))).thenAnswer(appel -> appel.getArgument(0));
        when(generateurCleApi.generer(Environnement.TEST)).thenReturn(CLE);
    }
}
