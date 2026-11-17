package dev.scx.app.x.config.environment.type;

import dev.scx.node.StringNode;
import dev.scx.object.NodeToObjectException;
import dev.scx.object.ObjectToNodeException;
import dev.scx.object.x.context.NodeToObjectContext;
import dev.scx.object.x.context.ObjectToNodeContext;
import dev.scx.object.x.mapper.TypeNodeMapper;
import dev.scx.reflect.TypeInfo;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import static dev.scx.reflect.ScxReflect.typeOf;

/// ConfiguredPathNodeMapper
///
/// @author scx567888
public final class ConfiguredPathNodeMapper implements TypeNodeMapper<ConfiguredPath, StringNode> {

    private final Path appRoot;

    public ConfiguredPathNodeMapper(Path appRoot) {
        this.appRoot = appRoot;
    }

    public static Path getPathByAppRoot(Path appRoot, String path) {
        Path result;
        if (path.startsWith("AppRoot:")) {
            result = appRoot.resolve(path.substring("AppRoot:".length()));
        } else {
            result = Path.of(path);
        }
        return result.toAbsolutePath().normalize();
    }

    @Override
    public TypeInfo valueType() {
        return typeOf(ConfiguredPath.class);
    }

    @Override
    public Class<StringNode> nodeType(NodeToObjectContext context) {
        return StringNode.class;
    }

    @Override
    public StringNode valueToNode(ConfiguredPath value, ObjectToNodeContext context) throws ObjectToNodeException {
        return new StringNode(value.path().toString());
    }

    @Override
    public ConfiguredPath nodeToValue(StringNode node, NodeToObjectContext context) throws NodeToObjectException {
        var value = node.value();
        try {
            var path = getPathByAppRoot(this.appRoot, value);
            return new ConfiguredPath(path);
        } catch (InvalidPathException e) {
            throw new NodeToObjectException(e);
        }
    }

}
