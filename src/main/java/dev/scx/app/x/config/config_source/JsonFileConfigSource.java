package dev.scx.app.x.config.config_source;

import dev.scx.ansi.Ansi;
import dev.scx.format.FormatToNodeException;
import dev.scx.node.Node;
import dev.scx.node.ObjectNode;
import dev.scx.serialize.ScxSerialize;

import java.io.File;
import java.io.IOException;

/// JsonFileConfigSource
///
/// @author scx567888
public final class JsonFileConfigSource implements ScxConfigSource {

    private final File jsonFile;
    private final ObjectNode value;

    private JsonFileConfigSource(File jsonFile) throws ScxConfigSourceException {
        this.jsonFile = jsonFile;
        this.value = loadFromJsonFile(this.jsonFile);
    }

    public static ObjectNode loadFromJsonFile(File jsonFile) throws ScxConfigSourceException {
        if (jsonFile == null) {
            throw new IllegalArgumentException("jsonFile 不能为空 !!!");
        }

        Node value;
        try {
            value = ScxSerialize.fromJson(jsonFile);
        } catch (IOException e) {
            throw new ScxConfigSourceException("配置文件读取失败!!! 请确保配置文件存在 : " + jsonFile, e);
        } catch (FormatToNodeException e) {
            throw new ScxConfigSourceException("配置文件已损坏!!! 请确保配置文件格式正确 : " + jsonFile, e);
        }

        if (!(value instanceof ObjectNode objectNode)) {
            throw new ScxConfigSourceException("配置文件必须为 Object 格式!!! 请确保配置文件格式正确 :" + jsonFile);
        }

        Ansi.ansi().brightBlue("✔ 已加载配置文件 : " + jsonFile).println();

        return objectNode;
    }

    public static JsonFileConfigSource of(File jsonFile) throws ScxConfigSourceException {
        return new JsonFileConfigSource(jsonFile);
    }

    @Override
    public ObjectNode value() {
        return value;
    }

    public File jsonFile() {
        return jsonFile;
    }

}
