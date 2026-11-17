package dev.scx.app.x.config.config_source;

import dev.scx.node.Node;
import dev.scx.node.ObjectNode;
import dev.scx.object.NodeToObjectException;
import dev.scx.object.ObjectToNodeException;

import static dev.scx.serialize.ScxSerialize.objectToNode;

/// ScxConfigSourceHelper
///
/// @author scx567888
final class ScxConfigSourceHelper {

    /// 根据配置路径获取节点
    public static Node getByPath(ObjectNode root, String path) {
        var parts = path.split("\\.");
        Node node = root;

        for (var part : parts) {
            if (node == null) {
                break;
            }
            if (node instanceof ObjectNode objectNode) {
                node = objectNode.get(part);
            } else {
                node = null;
            }
        }

        return node;
    }

    /// 根据配置路径设置节点
    public static void setByPath(ObjectNode root, String path, Object value) throws ObjectToNodeException, NodeToObjectException {
        var parts = path.split("\\.");
        var node = root;

        for (int i = 0; i < parts.length; i = i + 1) {
            var part = parts[i];
            // 最后一个路径段
            if (i == parts.length - 1) {
                node.put(part, objectToNode(value));
                continue;
            }

            // 获取中间路径段
            var n = node.get(part);
            // 没有中间段我们创建一个新的
            if (n == null) {
                var entries = new ObjectNode();
                node.put(part, entries);
                node = entries;
            } else if (n instanceof ObjectNode objectNode) {// 是 ObjectNode 继续走
                node = objectNode;
            } else {
                throw new IllegalArgumentException("路径中已有数据且不为 Object, 无法 set");
            }
        }
    }

}
