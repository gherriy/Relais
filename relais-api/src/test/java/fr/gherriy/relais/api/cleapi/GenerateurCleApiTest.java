package fr.gherriy.relais.api.cleapi;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class GenerateurCleApiTest {

    private final GenerateurCleApi generateur = new GenerateurCleApi();

    @Test
    void doitGenererUneCleDeTestAvecSonPrefixe() {
        CleGeneree cle = generateur.generer(Environnement.TEST);

        assertThat(cle.valeur()).startsWith("rl_test_").hasSize(8 + 43);
        assertThat(cle.prefixe()).hasSize(14);
        assertThat(cle.valeur()).startsWith(cle.prefixe());
    }

    @Test
    void doitStockerUnHashSha256EtJamaisLaCle() {
        CleGeneree cle = generateur.generer(Environnement.TEST);

        assertThat(cle.hash()).hasSize(64).matches("[0-9a-f]+");
        assertThat(cle.hash()).doesNotContain(cle.valeur());
    }

    @Test
    void doitGenererUneCleDifferenteAChaqueAppel() {
        CleGeneree premiere = generateur.generer(Environnement.TEST);
        CleGeneree seconde = generateur.generer(Environnement.TEST);

        assertThat(premiere.valeur()).isNotEqualTo(seconde.valeur());
        assertThat(premiere.hash()).isNotEqualTo(seconde.hash());
    }

    @Test
    void neDoitPasAfficherLaCleCompleteDansToString() {
        CleGeneree cle = generateur.generer(Environnement.TEST);

        assertThat(cle.toString()).doesNotContain(cle.valeur());
    }
}
