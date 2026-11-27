package fr.gherriy.relais.api.compte;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;


public interface CompteRepository extends JpaRepository<Compte, UUID> {
    boolean existsByEmail(String email);

}
