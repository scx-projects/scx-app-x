package dev.scx.app.x.logging;

import dev.scx.app.ScxAppModule;
import dev.scx.app.ScxAppModuleDefinition;
import dev.scx.app.environment.ScxEnvironment;
import dev.scx.app.environment.type.ConfiguredPath;
import dev.scx.logging.ScxLogging;

/// ScxAppLoggingModule
///
/// @author scx567888
/// @version 0.0.1
public final class ScxAppLoggingModule implements ScxAppModule {

    @Override
    public ScxAppModuleDefinition init(ScxEnvironment environment) {
        var defaultLoggingConfig = new LoggingConfig();
        defaultLoggingConfig.level = environment.get("scx.logging.default.level", String.class, "error");
        defaultLoggingConfig.type = environment.get("scx.logging.default.type", String.class, "console");
        defaultLoggingConfig.path = environment.get("scx.logging.default.path", ConfiguredPath.class, "AppRoot:logs");
        defaultLoggingConfig.trace = environment.get("scx.logging.default.trace", boolean.class, false);

        // 设置默认的 logging
        ScxLogging.rootConfig().updateConfig(defaultLoggingConfig.toLoggerConfig());

        // 设置具体的日志
        var loggers = environment.get("scx.logging.loggers", LoggingConfig[].class, new LoggingConfig[0]);

        for (var logger : loggers) {
            if (logger.name == null || logger.name.isBlank()) {
                continue;
            }

            // 这里 path 额外 回退到 默认配置的 路径
            if (logger.path == null) {
                logger.path = defaultLoggingConfig.path;
            }

            ScxLogging.setConfig(logger.name, logger.toLoggerConfig());
        }
        return ScxAppModuleDefinition.of();
    }

}
