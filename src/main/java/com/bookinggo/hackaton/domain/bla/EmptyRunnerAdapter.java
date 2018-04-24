package com.bookinggo.hackaton.domain.bla;

import org.apache.commons.lang3.NotImplementedException;

public class EmptyRunnerAdapter implements ScriptRunnerAdapter {
    
    @Override
    public Object runScript(String message, String sender, String channel) {
        throw new NotImplementedException("This adapter is not yet implemented");
    }
}
