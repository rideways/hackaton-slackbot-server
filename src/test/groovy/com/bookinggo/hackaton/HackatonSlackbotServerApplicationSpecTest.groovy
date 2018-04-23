package com.bookinggo.hackaton

import spock.lang.Specification
import spock.lang.Unroll

class ExampleSpecTest extends Specification {

    @Unroll
    "this test should #work"() {
        expect:
            true

        where:
            work << ["work", "work really well"]
    }

}
