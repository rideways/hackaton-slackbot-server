package com.bookinggo.hackaton.domain.slackapp;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Optional;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class SlackText {

    private final String firstWord;
    private final String secondWord;
    private final Optional<String> remainingText;
    private final Optional<String> codeFromURI;

    static SlackTextBuilder builder(String text) {
        return new SlackTextBuilder(text);
    }

    static class SlackTextBuilder {
        private final String text;

        SlackTextBuilder(String text) {
            this.text = text;
        }

        SlackText build() {
            String text = this.text.trim();

            int index = text.indexOf(" ");
            String firstWord = text.substring(0, index);

            String text2 = text.substring(index + 1)
                               .trim();
            int index2 = text2.indexOf(" ");

            if (index2 == -1) {
                return new SlackText(firstWord, text2, Optional.empty(), Optional.empty());
            }
            else {
                String secondWord = text2.substring(0, index2);
                Optional<String> remainingText = Optional.of(text2.substring(index2 + 1)
                                                                  .trim());

                Optional<String> code = remainingText.filter(s -> s.startsWith("<"))
                                                     .filter(s -> s.endsWith(">"))
                                                     .map(s -> s.substring(1, s.length() - 1))
                                                     .flatMap(this::getStringFromURI);

                return new SlackText(firstWord, secondWord, remainingText, code);
            }
        }

        private Optional<String> getStringFromURI(String uri) {
            try {
                return Optional.ofNullable(IOUtils.toString(new URI(uri), Charset.forName("UTF-8")));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        }

    }

}
