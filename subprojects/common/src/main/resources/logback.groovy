import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.filter.LevelFilter
import ch.qos.logback.core.ConsoleAppender

import static ch.qos.logback.core.spi.FilterReply.ACCEPT
import static ch.qos.logback.core.spi.FilterReply.DENY
import static ch.qos.logback.core.spi.FilterReply.NEUTRAL

// TODO: Change to XML file and compare startup times, or remove logback

def LEVEL = TRACE

appender('STDERR_ROOT', ConsoleAppender) {
    target = 'System.err'

    filter(LevelFilter) {
        level = ERROR
        onMatch = ACCEPT
        onMismatch = DENY
    }
    encoder(PatternLayoutEncoder) {
        pattern = 'qux! %msg%n'
    }
}
appender('STDOUT_ROOT', ConsoleAppender) {
    filter(LevelFilter) {
        level = WARN
        onMatch = ACCEPT
        onMismatch = NEUTRAL
    }
    filter(LevelFilter) {
        level = INFO
        onMatch = ACCEPT
        onMismatch = DENY
    }
    encoder(PatternLayoutEncoder) {
        pattern = 'qux: %msg%n'
    }
}
appender('STDOUT_ROOT_DEBUG', ConsoleAppender) {
    filter(LevelFilter) {
        level = DEBUG
        onMatch = ACCEPT
        onMismatch = NEUTRAL
    }
    filter(LevelFilter) {
        level = TRACE
        onMatch = ACCEPT
        onMismatch = DENY
    }
    encoder(PatternLayoutEncoder) {
        pattern = 'qux# %msg%n'
    }
}

root(LEVEL, ['STDOUT_ROOT', 'STDOUT_ROOT_DEBUG', 'STDERR_ROOT'])

// Production level should be set to WARN rather than anything else
if (LEVEL != WARN) {
    addWarn("logger level set to $LEVEL")
}

