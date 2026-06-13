package dev.scx.app.x.static_server;

import dev.scx.ansi.Ansi;

/// ScxAppStaticServerModuleHelper
///
/// @author scx567888
/// @version 0.0.1
final class ScxAppStaticServerModuleHelper {

    public static void printStaticServersInfo(StaticServer[] staticServers) {
        // 打印一下
        var o = Ansi.ansi().brightMagenta("已加载 " + staticServers.length + " 个 Static Server !!!").ln();

        for (var staticServer : staticServers) {
            var staticServerType = staticServer.staticServerType();
            var route = staticServer.route();
            var path = staticServer.path().path();

            o.brightYellow(staticServerType).brightCyan("    " + route).ln()
                .brightGreen("    " + path.toString()).ln();
        }

        o.print();
    }

}
