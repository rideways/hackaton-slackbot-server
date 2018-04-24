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
        "<https://raw.githubusercontent.com/rideways/hackaton-slackbot-server/master/src/test/groovy/com/bookinggo/hackaton/FileSpecification.groovy>"
    }

    private static String getRemainingText() {
        getURL()
    }

    private static String getCode() {
        "package com.bookinggo.hackaton\n" +
                "\n" +
                "import org.junit.runners.model.InitializationError\n" +
                "import spock.lang.Specification\n" +
                "\n" +
                "import static java.util.UUID.randomUUID\n" +
                "import static org.apache.commons.io.FileUtils.deleteDirectory\n" +
                "import static org.apache.commons.io.FileUtils.getTempDirectoryPath\n" +
                "\n" +
                "class FileSpecification extends Specification {\n" +
                "\n" +
                "    private static final String temporaryTestsRoot = \"\${tempDirectoryPath}/slack-bot-server-tests-\${randomUUID()}/\"\n" +
                "    private String temporaryTestRoot\n" +
                "\n" +
                "    def setupSpec() {\n" +
                "        createDirectories temporaryTestsRoot\n" +
                "    }\n" +
                "\n" +
                "    def setup() {\n" +
                "        temporaryTestRoot = \"\${temporaryTestsRoot}/\${randomUUID()}/\"\n" +
                "        createDirectories temporaryTestRoot\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    def cleanupSpec() {\n" +
                "        deleteDirectory new File(temporaryTestsRoot)\n" +
                "    }\n" +
                "\n" +
                "    protected getTestTemporaryPath() {\n" +
                "        return temporaryTestRoot\n" +
                "    }\n" +
                "\n" +
                "    protected static createDirectories(String path) {\n" +
                "        if (!new File(path).mkdirs()) {\n" +
                "            throw new InitializationError(\"Temporary testing directory '\${path}' could not be created\")\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "}\n"
    }


}
