package com.bookinggo.hackaton.domain.script;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ScriptRepository extends JpaRepository<ScriptEntity, Long> {

}
