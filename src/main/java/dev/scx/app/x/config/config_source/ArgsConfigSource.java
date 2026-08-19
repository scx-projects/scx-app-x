package dev.scx.app.x.config.config_source;

import dev.scx.node.ObjectNode;

import static dev.scx.app.x.config.config_source.ScxConfigSourceHelper.setByPath;

/// ArgsConfigSource
///
/// @author scx567888
public final class ArgsConfigSource implements ScxConfigSource {

    private final String[] args;
    private final ObjectNode value;

    private ArgsConfigSource(String... args) {
        this.args = args;
        this.value = loadFromArgs(this.args);
    }

    public static ObjectNode loadFromArgs(String... args) {
        var value = new ObjectNode();
        for (var arg : args) {
            if (arg.startsWith("--")) {
                var strings = arg.substring(2).split("=", 2);
                if (strings.length == 2) {
                    setByPath(value, strings[0], strings[1]);
                }
            }
        }
        return value;
    }

    public static ArgsConfigSource of(String... args) {
        return new ArgsConfigSource(args);
    }

    @Override
    public ObjectNode value() {
        return value;
    }

    public String[] args() {
        return args;
    }

}
