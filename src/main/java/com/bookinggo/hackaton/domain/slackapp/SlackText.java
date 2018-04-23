package com.bookinggo.hackaton.domain.slackapp;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class SlackText {

    private final String firstWord;
    private final String secondWord;
    private final Optional<String> remainingText;

    static SlackTextBuilder builder(String text) {
        return new SlackTextBuilder(text);
    }

    static class SlackTextBuilder {
        private final String text;

        public SlackTextBuilder(String text) {
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
                return new SlackText(firstWord, text2, Optional.empty());
            } else {
                String secondWord = text2.substring(0, index2);
                Optional<String> remainingText = Optional.of(text2.substring(index2 + 1)
                                                                  .trim());
                return new SlackText(firstWord, secondWord, remainingText);
            }

        }

    }
}
