package dev.scx.app.x.static_server;

/// StaticServerType
///
/// @author scx567888
enum StaticServerType {

    STATIC_FILES("STATIC-FILES"),
    SINGLE_FILE("SINGLE-FILE ");// 末尾加一个空格 让 toString 对齐

    private final String display;

    StaticServerType(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }

}
