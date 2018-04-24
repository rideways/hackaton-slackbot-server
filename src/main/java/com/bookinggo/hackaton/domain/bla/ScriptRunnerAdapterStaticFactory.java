package com.bookinggo.hackaton.domain.bla;

import java.util.Optional;

public class ScriptRunnerAdapterStaticFactory {

    public static ScriptRunnerAdapter createAdapter(String scriptCode, String language) {
        ScriptLanguage scriptLanguage = Optional.of(language)
                                                .map(ScriptLanguage::fromString)
                                                .orElseThrow(() -> new RuntimeException("Script language " + language + " is not supported"));

        switch (scriptLanguage) {
        case GROOVY:
            return new GroovyRunnerAdapter(scriptCode);
        default:
            return new EmptyRunnerAdapter();
        }
    }

}
