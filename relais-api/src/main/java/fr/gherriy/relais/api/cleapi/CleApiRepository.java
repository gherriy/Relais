package fr.gherriy.relais.api.cleapi;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;


public interface CleApiRepository extends JpaRepository<CleApi, UUID> {}
