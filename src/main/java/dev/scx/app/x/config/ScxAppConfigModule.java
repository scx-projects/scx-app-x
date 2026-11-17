package dev.scx.app.x.config;

import dev.scx.app.ScxApp;
import dev.scx.app.ScxAppModule;
import dev.scx.app.x.config.code_source.ScxCodeSource;
import dev.scx.app.x.config.config_source.ArgsConfigSource;
import dev.scx.app.x.config.config_source.JsonFileConfigSource;
import dev.scx.app.x.config.config_source.MapConfigSource;
import dev.scx.app.x.config.environment.ScxEnvironment;
import dev.scx.app.x.config.environment.type.ConfiguredPath;

import java.util.Map;

/// ScxAppConfigModule
///
/// @author scx567888
public final class ScxAppConfigModule implements ScxAppModule {

    private static final Map<String, ?> DEFAULT_CONFIG = Map.of("scx.config", "AppRoot:scx-config.json");

    /// 外部参数
    private final String[] args;

    /// mainClass
    private final Class<?> mainClass;

    /// 环境
    private ScxEnvironment environment;

    public ScxAppConfigModule(Class<?> mainClass, String... args) {
        if (mainClass == null) {
            throw new IllegalArgumentException("MainClass must not be null !!!");
        }
        this.mainClass = mainClass;
        this.args = args;
    }

    @Override
    public void start(ScxApp scxApp) throws Exception {
        // 1, 创建 codeSource
        var codeSource = ScxCodeSource.of(this.mainClass);

        // 2, 根据 默认配置源 + args 配置源, 创建 bootstrap 环境
        var defaultConfigSource = MapConfigSource.of(DEFAULT_CONFIG);
        var argsConfigSource = ArgsConfigSource.of(this.args);
        var bootstrapEnvironment = ScxEnvironment.of(codeSource.baseDirectory(), defaultConfigSource, argsConfigSource);

        // 3, 获取 配置文件路径 创建 json 文件配置源.
        var configPath = bootstrapEnvironment.get("scx.config", ConfiguredPath.class);
        var jsonFileConfigSource = JsonFileConfigSource.of(configPath.path().toFile());

        // 4, 创建真正的 环境
        this.environment = ScxEnvironment.of(codeSource.baseDirectory(), defaultConfigSource, jsonFileConfigSource, argsConfigSource);
    }

    public ScxEnvironment environment() {
        if (environment == null) {
            throw new IllegalStateException(name() + " has not started");
        }
        return environment;
    }

}
