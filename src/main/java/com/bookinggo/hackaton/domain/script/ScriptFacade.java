package com.bookinggo.hackaton.domain.script;

import com.bookinggo.hackaton.domain.script.dto.ScriptDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ScriptFacade {

    private final ScriptService scriptService;

    public Long saveScript(ScriptDto scriptDto) {
        return scriptService.createScript(scriptDto)
                            .blockingGet();
    }

    public Long updateScript(Long scriptId, ScriptDto scriptDto) {
        return scriptService.updateScript(scriptId, scriptDto)
                            .blockingGet();
    }

}
