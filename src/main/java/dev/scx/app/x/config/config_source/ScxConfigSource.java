package dev.scx.app.x.config.config_source;

import dev.scx.node.Node;
import dev.scx.node.ObjectNode;

/// 配置源
///
/// @author scx567888
public interface ScxConfigSource {

    /// 获取当前配置值
    ///
    /// 注意配置值必须被归一化为 树形结构.
    /// 换句话说 所有 "." 分隔符都应看作 树形结构的一层.
    ///
    /// @return 当前配置值
    ObjectNode value();

    default Node get(String path) {
        return ScxConfigSourceHelper.getByPath(value(), path);
    }

}
