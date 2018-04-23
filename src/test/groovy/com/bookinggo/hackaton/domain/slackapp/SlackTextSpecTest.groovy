package com.bookinggo.hackaton.domain.slackapp

import spock.lang.Specification
import spock.lang.Unroll

class SlackTextSpecTest extends Specification {

    @Unroll
    def "when receive text [#text], should identify first: [#expectedFirstWord], second: [#expectedSecondWord] and others: [#expectedtext]"() {
        when:
            def slackText = SlackText.builder(text).build()

        then:
            slackText.getFirstWord() == expectedFirstWord
            slackText.getSecondWord() == expectedSecondWord
            slackText.getRemainingText() == expectedtext

        where:
            text                                                        || expectedFirstWord | expectedSecondWord | expectedtext
            "add name public class{sdsajd asdsadsad} () fsfddsfs dasd"  || "add"             | "name"             | "public class{sdsajd asdsadsad} () fsfddsfs dasd"
            "add name  public class{sdsajd asdsadsad} () fsfddsfs dasd" || "add"             | "name"             | "public class{sdsajd asdsadsad} () fsfddsfs dasd"
            "run bla param1 param2 "                                    || "run"             | "bla"              | "param1 param2"
            "run bla"                                                   || "run"             | "bla"              | null

    }
}
