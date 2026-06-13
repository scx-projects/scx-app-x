package dev.scx.app.x.static_server;

/// StaticServerType
///
/// @author scx567888
/// @version 0.0.1
enum StaticServerType {

    STATIC_FILES("STATIC-FILES"),
    SINGLE_FILE("SINGLE-FILE ");

    private final String display;

    StaticServerType(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }

}
