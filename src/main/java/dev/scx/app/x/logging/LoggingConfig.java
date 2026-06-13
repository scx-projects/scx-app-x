package dev.scx.app.x.logging;

import dev.scx.app.environment.type.ConfiguredPath;
import dev.scx.logging.ScxLoggerConfig;
import dev.scx.logging.recorder.ConsoleRecorder;
import dev.scx.logging.recorder.FileRecorder;

import static java.lang.System.Logger.Level.*;

/// LoggingConfig
///
/// @author scx567888
/// @version 0.0.1
final class LoggingConfig {

    public String name;
    public String level;
    public String type;
    public ConfiguredPath path;
    public Boolean trace;

    public System.Logger.Level toLevel() {
        if (level == null) {
            return null;
        }
        var l = level.trim().toUpperCase();
        return switch (l) {
            case "OFF", "O" -> OFF;
            case "ERROR", "E" -> ERROR;
            case "WARN", "WARNING", "W" -> WARNING;
            case "INFO", "I" -> INFO;
            case "DEBUG", "D" -> DEBUG;
            case "TRACE", "T" -> TRACE;
            case "ALL", "A" -> ALL;
            default -> throw new IllegalArgumentException("不支持的 logger 级别 : " + level);
        };
    }

    public LoggingType toType() {
        // 允许 null
        if (type == null) {
            return null;
        }
        var t = type.trim().toUpperCase();
        return switch (t) {
            case "CONSOLE", "C" -> LoggingType.CONSOLE;
            case "FILE", "F" -> LoggingType.FILE;
            case "BOTH", "B" -> LoggingType.BOTH;
            default -> throw new IllegalArgumentException("不支持的 logger 类型 : " + type);
        };
    }

    public ScxLoggerConfig toLoggerConfig() {
        var loggerConfig = new ScxLoggerConfig();
        loggerConfig.setLevel(this.toLevel());
        var type = this.toType();
        if (type == LoggingType.CONSOLE || type == LoggingType.BOTH) {
            loggerConfig.addRecorder(new ConsoleRecorder());
        }
        if (type == LoggingType.FILE || type == LoggingType.BOTH) {
            loggerConfig.addRecorder(new FileRecorder(path.path()));
        }
        loggerConfig.setStackTrace(trace);
        return loggerConfig;
    }

}
