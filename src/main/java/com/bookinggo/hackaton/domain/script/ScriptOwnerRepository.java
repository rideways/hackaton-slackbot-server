package com.bookinggo.hackaton.domain.script;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface ScriptOwnerRepository extends JpaRepository<ScriptOwnerEntity, Long> {

    Optional<ScriptOwnerEntity> findByUsername(String username);

}
