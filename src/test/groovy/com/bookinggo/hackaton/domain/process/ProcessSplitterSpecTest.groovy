package com.bookinggo.hackaton.domain.process

import com.sun.akuma.Daemon
import com.sun.akuma.JavaVMArguments
import spock.lang.Specification

class ProcessSplitterSpecTest extends Specification {

    def daemon = Mock Daemon
    def argumentsProvider = Mock JvmArgumentsProvider

    def "should run the child process flow if daemonized"() {
        given:
            def childFlow = Mock Runnable

        when:
            ProcessSplitter.splitProcess(daemon, argumentsProvider, { /* do nothing */ }, childFlow)

        then: "daemon is initialized"
            1 * daemon.isDaemonized() >> true
            1 * daemon.init()

        and: "child flow runs"
            1 * childFlow.run()
    }

    def "should append JVM arguments to the current JVM arguments"() {
        given:
            def customJvmArgs = [test: "oneTwoThree"]

        when:
            ProcessSplitter.splitProcess(daemon, argumentsProvider, { forker -> forker.fork(customJvmArgs) }, {/* do nothing */ })

        then: "use the parent flow"
            1 * daemon.isDaemonized() >> false

        and: "daemonize with custom JVM arg"
            1 * argumentsProvider.getCurrentArguments() >> new JavaVMArguments(["-DalreadyExistingArgument=heyoo"])
            1 * daemon.daemonize(_ as JavaVMArguments) >> { List args ->
                assert (args[0] as List<String>).contains("-Dtest=oneTwoThree")
            }
    }

    def "should not throw if there are no JVM arguments but we are not passing any either"() {
        when:
            ProcessSplitter.splitProcess(daemon, argumentsProvider, { forker -> forker.fork() }, {/* do nothing */ })

        then: "use the parent flow"
            1 * daemon.isDaemonized() >> false

        and: "daemonize with no custom JVM arg"
            1 * argumentsProvider.getCurrentArguments() >> new JavaVMArguments()
            1 * daemon.daemonize(_ as JavaVMArguments) >> { List args ->
                assert (args[0] as List<String>).empty
            }
    }

    def "should throw if we are passing a custom JVM argument but none were present before"() {
        given:
            def customJvmArgs = [test: "oneTwoThree"]

        when:
            ProcessSplitter.splitProcess(daemon, argumentsProvider, { forker -> forker.fork(customJvmArgs) }, {/* do nothing */ })

        then: "use the parent flow"
            1 * daemon.isDaemonized() >> false

        and: "daemonize with no custom JVM arg"
            1 * argumentsProvider.getCurrentArguments() >> new JavaVMArguments()
            0 * daemon.daemonize(_ as JavaVMArguments)

        and:
            thrown JvmArgumentsConcatenationException
    }

}
