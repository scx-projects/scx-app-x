package dev.scx.app.x.config.environment.type;

import dev.scx.node.NumberNode;
import dev.scx.node.StringNode;
import dev.scx.node.ValueNode;
import dev.scx.object.NodeToObjectException;
import dev.scx.object.ObjectToNodeException;
import dev.scx.object.x.context.NodeToObjectContext;
import dev.scx.object.x.context.ObjectToNodeContext;
import dev.scx.object.x.mapper.TypeNodeMapper;
import dev.scx.reflect.TypeInfo;

import static dev.scx.reflect.ScxReflect.typeOf;

/// ConfiguredSizeNodeMapper
///
/// @author scx567888
public final class ConfiguredSizeNodeMapper implements TypeNodeMapper<ConfiguredSize, ValueNode> {

    @Override
    public TypeInfo valueType() {
        return typeOf(ConfiguredSize.class);
    }

    @Override
    public Class<ValueNode> nodeType(NodeToObjectContext context) {
        return ValueNode.class;
    }

    @Override
    public ValueNode valueToNode(ConfiguredSize value, ObjectToNodeContext context) throws ObjectToNodeException {
        var v = ConfiguredSizeHelper.longToDisplaySize(value.size());
        return new StringNode(v);
    }

    @Override
    public ConfiguredSize nodeToValue(ValueNode node, NodeToObjectContext context) throws NodeToObjectException {
        if (node instanceof NumberNode) {
            try {
                // 这里我们永远使用 精确转换
                return new ConfiguredSize(node.asLongExact());
            } catch (NumberFormatException | ArithmeticException e) {
                throw new NodeToObjectException(e);
            }
        }
        if (node instanceof StringNode) {
            try {
                // 这里我们永远使用 精确转换
                return new ConfiguredSize(ConfiguredSizeHelper.displaySizeToLong(node.asString()));
            } catch (IllegalArgumentException e) {
                throw new NodeToObjectException(e);
            }
        }
        throw new NodeToObjectException("不支持的 Node 类型 : " + node.getClass().getSimpleName());
    }

}
