package dev.scx.app.x.logging;

import dev.scx.app.ScxAppModule;
import dev.scx.app.ScxAppModuleDefinition;
import dev.scx.app.environment.ScxEnvironment;
import dev.scx.app.environment.type.ConfiguredPath;
import dev.scx.logging.ScxLogging;

import java.util.regex.Pattern;

/// ScxAppLoggingModule
///
/// @author scx567888
public final class ScxAppLoggingModule implements ScxAppModule {

    @Override
    public ScxAppModuleDefinition init(ScxEnvironment environment) {
        var defaultLoggingConfig = new LoggingConfig();
        defaultLoggingConfig.level = environment.get("scx.logging.default.level", String.class, "error");
        defaultLoggingConfig.type = environment.get("scx.logging.default.type", String.class, "console");
        defaultLoggingConfig.path = environment.get("scx.logging.default.path", ConfiguredPath.class, "AppRoot:logs");
        defaultLoggingConfig.trace = environment.get("scx.logging.default.trace", boolean.class, false);

        // 设置默认的 logging
        ScxLogging.rootConfig(defaultLoggingConfig.toLoggerConfig());

        // 设置具体的日志
        var loggers = environment.get("scx.logging.loggers", LoggingConfig[].class, new LoggingConfig[0]);

        for (var logger : loggers) {
            // 这里判断是 使用 name 还是 regex, 不允许都没设置 也不允许都设置.
            var hasName = logger.name != null && !logger.name.isBlank();
            var hasRegex = logger.regex != null && !logger.regex.isBlank();

            if (!hasName && !hasRegex) {
                throw new IllegalArgumentException("logger 配置错误: name 和 regex 必须设置其中一个");
            }

            if (hasName && hasRegex) {
                throw new IllegalArgumentException("logger 配置错误: name 和 regex 不能同时设置");
            }

            // 这里 path 额外 回退到 默认配置的 路径
            if (logger.path == null) {
                logger.path = defaultLoggingConfig.path;
            }

            if (hasName) {
                ScxLogging.config(logger.name, logger.toLoggerConfig());
            } else {
                ScxLogging.config(Pattern.compile(logger.regex), logger.toLoggerConfig());
            }
        }
        return ScxAppModuleDefinition.of();
    }

}
