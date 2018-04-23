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
            "add name public class{sdsajd asdsadsad} () fsfddsfs dasd"  || "add"             | "name"             | Optional.of("public class{sdsajd asdsadsad} () fsfddsfs dasd")
            "add name  public class{sdsajd asdsadsad} () fsfddsfs dasd" || "add"             | "name"             | Optional.of("public class{sdsajd asdsadsad} () fsfddsfs dasd")
            "run bla param1 param2 "                                    || "run"             | "bla"              | Optional.of("param1 param2")
            "run bla"                                                   || "run"             | "bla"              | Optional.empty()

    }

    def "text url code parsers"() {
        when:
            def slackText = SlackText.builder(text).build()

        then:
            slackText.getFirstWord() == expectedFirstWord
            slackText.getSecondWord() == expectedSecondWord
            slackText.getRemainingText() == expectedtext
            slackText.getCodeFromURI() == expectedCode

        where:
            text                    || expectedFirstWord | expectedSecondWord | expectedtext                    | expectedCode
            "addu name " + getURL() || "addu"            | "name"             | Optional.of(getRemainingText()) | Optional.of(getCode())
    }

    private static String getURL() {
        "<https://raw.githubusercontent.com/rideways/hackaton-slackbot-server/master/src/main/java/com/bookinggo/hackaton/HackatonSlackbotServerApplication.java>"
    }

    private static String getRemainingText() {
        getURL()
    }

    private static String getCode() {
        "package com.bookinggo.hackaton;\n" +
                "\n" +
                "import org.springframework.boot.SpringApplication;\n" +
                "import org.springframework.boot.autoconfigure.SpringBootApplication;\n" +
                "\n" +
                "@SpringBootApplication\n" +
                "public class HackatonSlackbotServerApplication {\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        SpringApplication.run(HackatonSlackbotServerApplication.class, args);\n" +
                "    }\n" +
                "}\n"
    }


}
