package fr.gherriy.relais.api.compte;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.jayway.jsonpath.JsonPath;

import fr.gherriy.relais.api.TestcontainersConfiguration;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class InscriptionIntegrationTest {

    private static final String MOT_DE_PASSE = "motdepasse-solide";

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private JdbcClient jdbc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void doitEnregistrerLeCompteEtSaCleDeTestSansStockerDeSecret() throws Exception {
        String email = emailUnique();

        MvcTestResult resultat = inscrire("Mon projet", email);

        assertThat(resultat).hasStatus(HttpStatus.CREATED);
        String corps = resultat.getResponse().getContentAsString(StandardCharsets.UTF_8);
        UUID compteId = UUID.fromString(JsonPath.read(corps, "$.compteId"));
        String cleApi = JsonPath.read(corps, "$.cleApi");

        Map<String, Object> compte = jdbc.sql("SELECT nom_projet, email, mot_de_passe_hash FROM compte WHERE id = ?")
                .param(compteId)
                .query().singleRow();
        assertThat(compte.get("nom_projet")).isEqualTo("Mon projet");
        assertThat(compte.get("email")).isEqualTo(email);
        String motDePasseHash = (String) compte.get("mot_de_passe_hash");
        assertThat(motDePasseHash).isNotEqualTo(MOT_DE_PASSE).startsWith("$2");
        assertThat(passwordEncoder.matches(MOT_DE_PASSE, motDePasseHash)).isTrue();

        Map<String, Object> cle = jdbc.sql("SELECT environnement, prefixe, hash FROM cle_api WHERE compte_id = ?")
                .param(compteId)
                .query().singleRow();
        assertThat(cleApi).startsWith("rl_test_");
        assertThat(cle.get("environnement")).isEqualTo("TEST");
        assertThat(cleApi).startsWith((String) cle.get("prefixe"));
        assertThat(cle.get("hash")).isEqualTo(sha256(cleApi));
    }

    @Test
    void doitRefuserUnEmailDejaUtiliseQuelleQueSoitLaCasse() {
        String email = emailUnique();
        assertThat(inscrire("Premier projet", email)).hasStatus(HttpStatus.CREATED);

        MvcTestResult resultat = inscrire("Second projet", email.toUpperCase());

        assertThat(resultat).hasStatus(HttpStatus.CONFLICT);
        Long comptes = jdbc.sql("SELECT count(*) FROM compte WHERE email = ?")
                .param(email)
                .query(Long.class).single();
        assertThat(comptes).isEqualTo(1);
    }

    private MvcTestResult inscrire(String nomProjet, String email) {
        return mvc.post().uri("/auth/inscription")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nomProjet": "%s", "email": "%s", "motDePasse": "%s"}
                        """.formatted(nomProjet, email, MOT_DE_PASSE))
                .exchange();
    }

    private static String emailUnique() {
        return "dev-" + UUID.randomUUID() + "@exemple.fr";
    }

    private static String sha256(String valeur) throws Exception {
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(valeur.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash);
    }
}
