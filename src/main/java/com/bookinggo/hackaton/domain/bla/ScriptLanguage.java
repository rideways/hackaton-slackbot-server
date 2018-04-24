package com.bookinggo.hackaton.domain.bla;

import static java.util.Objects.*;

public enum ScriptLanguage {

    GROOVY,
    PYTHON;

    public static ScriptLanguage fromString(String language) {
        requireNonNull(language);
        for (ScriptLanguage scriptLanguage : ScriptLanguage.values()) {
            if (language.equalsIgnoreCase(scriptLanguage.name())) {
                return scriptLanguage;
            }
        }
        return null;
    }

}
