package com.bookinggo.hackaton

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@DataJpaTest
@ContextConfiguration
@ActiveProfiles("database-test")
class DatabaseSpecification {
}
