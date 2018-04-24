package com.bookinggo.hackaton.domain.process;

import com.sun.akuma.JavaVMArguments;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import static lombok.AccessLevel.PACKAGE;

@NoArgsConstructor(access = PACKAGE)
class JvmArgumentsProvider {

    @SneakyThrows
    JavaVMArguments getCurrentArguments() {
        return JavaVMArguments.current();
    }

}
