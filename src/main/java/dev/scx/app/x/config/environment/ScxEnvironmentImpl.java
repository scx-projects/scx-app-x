package dev.scx.app.x.config.environment;

import dev.scx.app.x.config.config_source.ScxConfigSource;
import dev.scx.app.x.config.environment.type.ConfiguredPathNodeMapper;
import dev.scx.app.x.config.environment.type.ConfiguredSizeNodeMapper;
import dev.scx.node.Node;
import dev.scx.object.x.DefaultObjectNodeConvertConfig;
import dev.scx.object.x.DefaultObjectNodeConvertOptions;
import dev.scx.object.x.DefaultObjectNodeConverter;
import dev.scx.reflect.TypeInfo;

import java.nio.file.Path;

import static dev.scx.node.NullNode.NULL;

/// ScxEnvironmentImpl
///
/// @author scx567888
final class ScxEnvironmentImpl implements ScxEnvironment {

    private static final DefaultObjectNodeConvertOptions CONVERT_OPTIONS = DefaultObjectNodeConvertConfig.of();

    private final Path appRoot;
    private final ScxConfigSource[] sources;
    private final DefaultObjectNodeConverter converter;

    public ScxEnvironmentImpl(Path appRoot, ScxConfigSource... sources) {
        this.appRoot = appRoot;
        this.sources = sources;
        this.converter = DefaultObjectNodeConverter.builder()
            .registerDefaultMappers()
            .registerMapper(new ConfiguredPathNodeMapper(this.appRoot))
            .registerMapper(new ConfiguredSizeNodeMapper())
            .build();
    }

    @Override
    public Path appRoot() {
        return this.appRoot;
    }

    @Override
    public Node get(String path) {
        for (int i = this.sources.length - 1; i >= 0; i = i - 1) {
            var source = this.sources[i];
            var node = source.get(path);
            if (node != null) {
                return node;
            }
        }
        return null;
    }

    @Override
    public <T> T get(String path, Class<T> type) {
        var node = get(path);
        if (node == null) {
            node = NULL;
        }
        return converter.nodeToObject(node, type, CONVERT_OPTIONS);
    }

    @Override
    public <T> T get(String path, TypeInfo type) {
        var node = get(path);
        if (node == null) {
            node = NULL;
        }
        return converter.nodeToObject(node, type, CONVERT_OPTIONS);
    }

    @Override
    public <T> T get(String path, Class<T> type, Object defaultValue) {
        var node = get(path);
        if (node == null) {
            node = converter.objectToNode(defaultValue, CONVERT_OPTIONS);
        }
        return converter.nodeToObject(node, type, CONVERT_OPTIONS);
    }

    @Override
    public <T> T get(String path, TypeInfo type, Object defaultValue) {
        var node = get(path);
        if (node == null) {
            node = converter.objectToNode(defaultValue, CONVERT_OPTIONS);
        }
        return converter.nodeToObject(node, type, CONVERT_OPTIONS);
    }

    @Override
    public <T> T convertObject(Object value, Class<T> type) {
        var node = converter.objectToNode(value, CONVERT_OPTIONS);
        return converter.nodeToObject(node, type, CONVERT_OPTIONS);
    }

    @Override
    public <T> T convertObject(Object value, TypeInfo type) {
        var node = converter.objectToNode(value, CONVERT_OPTIONS);
        return converter.nodeToObject(node, type, CONVERT_OPTIONS);
    }

}
