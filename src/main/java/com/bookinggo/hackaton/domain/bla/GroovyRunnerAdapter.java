package com.bookinggo.hackaton.domain.bla;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GroovyRunnerAdapter implements ScriptRunnerAdapter {

    private final Closure<?> script;

    public GroovyRunnerAdapter(String scriptCode) {
        script = generateClosure(scriptCode);
    }

    private Closure<?> generateClosure(String groovyCode) {
        Binding binding = new Binding();
        GroovyShell shell = new GroovyShell(binding);
        return (Closure<?>) shell.evaluate(groovyCode);
    }

    @Override
    public Object runScript(String message, String sender, String channel) {
        return script.call(message, sender, channel);
    }

}
