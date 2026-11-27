package fr.gherriy.relais.api.compte;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import fr.gherriy.relais.api.compte.dto.InscriptionResponse;
import fr.gherriy.relais.api.securite.SecurityConfig;

@WebMvcTest(InscriptionController.class)
@Import(SecurityConfig.class)
class InscriptionControllerTest {

    private static final String DEMANDE_VALIDE = """
            {"nomProjet": "Mon projet", "email": "dev@exemple.fr", "motDePasse": "motdepasse-solide"}
            """;

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private InscriptionService inscriptionService;

    @Test
    void doitRenvoyer201AvecLaCleSansAuthentification() {
        UUID compteId = UUID.randomUUID();
        when(inscriptionService.inscrire(any()))
                .thenReturn(new InscriptionResponse(compteId, "Mon projet", "dev@exemple.fr", "rl_test_abcdefSECRET"));

        MvcTestResult resultat = inscrire(DEMANDE_VALIDE);

        assertThat(resultat).hasStatus(HttpStatus.CREATED);
        assertThat(resultat).bodyJson().extractingPath("$.compteId").isEqualTo(compteId.toString());
        assertThat(resultat).bodyJson().extractingPath("$.cleApi").isEqualTo("rl_test_abcdefSECRET");
        assertThat(resultat.getResponse().getHeader("Cache-Control")).contains("no-store");
    }

    @Test
    void doitRenvoyer409QuandLEmailEstDejaUtilise() {
        when(inscriptionService.inscrire(any())).thenThrow(new EmailDejaUtiliseException());

        MvcTestResult resultat = inscrire(DEMANDE_VALIDE);

        assertThat(resultat).hasStatus(HttpStatus.CONFLICT);
        assertThat(resultat).bodyJson().extractingPath("$.title").isEqualTo("Email déjà utilisé");
        assertThat(resultat).bodyJson().extractingPath("$.detail").isEqualTo("Un compte existe déjà avec cet email");
    }

    @Test
    void doitRenvoyer400AvecUneErreurParChampInvalide() {
        MvcTestResult resultat = inscrire("""
                {"nomProjet": "Mon projet", "email": "pas-un-email", "motDePasse": "court"}
                """);

        assertThat(resultat).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(resultat).bodyJson().extractingPath("$.erreurs.email").isEqualTo("L'email n'est pas valide");
        assertThat(resultat).bodyJson().extractingPath("$.erreurs.motDePasse")
                .isEqualTo("Le mot de passe doit contenir entre 12 et 72 caractères");
        verifyNoInteractions(inscriptionService);
    }

    @Test
    void doitRenvoyer400QuandLeCorpsEstIllisible() {
        MvcTestResult resultat = inscrire("{\"nomProjet\":");

        assertThat(resultat).hasStatus(HttpStatus.BAD_REQUEST);
        verifyNoInteractions(inscriptionService);
    }

    @Test
    void doitRefuserUneRouteProtegeeSansAuthentification() {
        assertThat(mvc.get().uri("/v1/notifications")).hasStatus(HttpStatus.UNAUTHORIZED);
    }

    private MvcTestResult inscrire(String corps) {
        return mvc.post().uri("/auth/inscription")
                .contentType(MediaType.APPLICATION_JSON)
                .content(corps)
                .exchange();
    }
}
