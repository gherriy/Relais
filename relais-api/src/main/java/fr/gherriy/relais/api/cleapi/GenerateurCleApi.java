package fr.gherriy.relais.api.cleapi;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

import org.springframework.stereotype.Component;

@Component
public class GenerateurCleApi {

    private static final int OCTETS_ALEATOIRES = 32;
    private static final int CARACTERES_VISIBLES = 6;

    private final SecureRandom aleatoire = new SecureRandom();

    public CleGeneree generer(Environnement environnement) {
        byte[] octets = new byte[OCTETS_ALEATOIRES];
        aleatoire.nextBytes(octets);
        String secret = Base64.getUrlEncoder().withoutPadding().encodeToString(octets);

        String valeur = environnement.prefixe() + secret;
        String prefixe = environnement.prefixe() + secret.substring(0, CARACTERES_VISIBLES);
        return new CleGeneree(valeur, prefixe, hacher(valeur));
    }

    private String hacher(String valeur) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(valeur.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 est indisponible sur cette JVM", e);
        }
    }
}
