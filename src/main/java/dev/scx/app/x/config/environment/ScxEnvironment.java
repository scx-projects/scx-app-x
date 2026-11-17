package dev.scx.app.x.config.environment;

import dev.scx.app.x.config.config_source.ScxConfigSource;
import dev.scx.node.Node;
import dev.scx.reflect.TypeInfo;
import dev.scx.reflect.TypeReference;

import java.nio.file.Path;

import static dev.scx.reflect.ScxReflect.typeOf;

/// ScxEnvironment
///
/// @author scx567888
public interface ScxEnvironment {

    /// sources 按添加顺序保存.
    /// 查询时后添加的 ConfigSource 优先级更高.
    static ScxEnvironment of(Path appRoot, ScxConfigSource... sources) {
        return new ScxEnvironmentImpl(appRoot, sources);
    }

    /// 应用根路径
    Path appRoot();

    Node get(String path);

    <T> T get(String path, Class<T> type);

    <T> T get(String path, TypeInfo type);

    <T> T get(String path, Class<T> type, Object defaultValue);

    <T> T get(String path, TypeInfo type, Object defaultValue);

    <T> T convertObject(Object value, Class<T> type);

    <T> T convertObject(Object value, TypeInfo type);

    default <T> T get(String path, TypeReference<T> type) {
        return get(path, typeOf(type));
    }

    default <T> T get(String path, TypeReference<T> type, Object defaultValue) {
        return get(path, typeOf(type), defaultValue);
    }

    default <T> T convertObject(Object value, TypeReference<T> type) {
        return convertObject(value, typeOf(type));
    }

}
