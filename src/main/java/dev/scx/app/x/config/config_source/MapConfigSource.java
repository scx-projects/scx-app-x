package dev.scx.app.x.config.config_source;

import dev.scx.node.ObjectNode;

import java.util.Map;

import static dev.scx.app.x.config.config_source.ScxConfigSourceHelper.setByPath;

/// MapConfigSource
///
/// @author scx567888
public final class MapConfigSource implements ScxConfigSource {

    private final Map<String, ?> map;
    private final ObjectNode value;

    private MapConfigSource(Map<String, ?> map) {
        this.map = map;
        this.value = loadFromMap(this.map);
    }

    public static ObjectNode loadFromMap(Map<String, ?> map) {
        var value = new ObjectNode();
        map.forEach((k, v) -> setByPath(value, k, v));
        return value;
    }

    public static MapConfigSource of(Map<String, ?> map) {
        return new MapConfigSource(map);
    }

    @Override
    public ObjectNode value() {
        return value;
    }

    public Map<String, ?> map() {
        return map;
    }

}
