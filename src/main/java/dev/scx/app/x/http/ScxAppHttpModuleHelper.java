package dev.scx.app.x.http;

import dev.scx.ansi.Ansi;
import dev.scx.collection.ScxCollection;
import dev.scx.http.routing.Router;
import dev.scx.http.routing.request_matcher.TypeIsRequestMatcher;
import dev.scx.websocket.x.ScxServerWebSocketHandshakeRequest;

import java.net.*;

import static java.net.NetworkInterface.networkInterfaces;

/// ScxAppHttpModuleHelper
///
/// @author scx567888
/// @version 0.0.1
final class ScxAppHttpModuleHelper {

    public static void printRouteInfo(Router router) {
        var routeEntries = router.routeTable().entries();

        // 分组 路由
        var groupedRoutes = ScxCollection.groupingBy(routeEntries, (c) -> {
            var requestMatcher = c.route().requestMatcher();
            // 这里我们采用一种比较粗略的方式
            if (requestMatcher instanceof TypeIsRequestMatcher(
                Class<? extends dev.scx.http.ScxHttpServerRequest> requestType
            ) && requestType == ScxServerWebSocketHandshakeRequest.class) {
                return "WEBSOCKET";
            }
            // 其余全部算作 普通 HTTP
            return "HTTP";
        });

        var httpRoutes = groupedRoutes.getAll("HTTP");
        var webSocketRoutes = groupedRoutes.getAll("WEBSOCKET");

        Ansi.ansi()
            .brightGreen("已加载 " + httpRoutes.size() + " 个 HTTP 路由 !!!").ln()
            .brightBlue("已加载 " + webSocketRoutes.size() + " 个 WebSocket 路由 !!!")
            .println();
    }

    public static void printServerAddresses(InetSocketAddress httpServiceAddress, boolean sslEnabled) throws SocketException {
        var httpOrHttps = sslEnabled ? "https" : "http";
        var realPort = httpServiceAddress.getPort();

        var o = Ansi.ansi().green("HTTP 服务器启动成功 !!!").ln();

        o.green("> 本地: " + httpOrHttps + "://localhost:" + realPort + "/").ln();

        // 获取本机的 IP 地址 (不包括回环地址)
        var localIPv4Addresses = networkInterfaces()
            .flatMap(NetworkInterface::inetAddresses)
            .filter(c -> !c.isLoopbackAddress() && c instanceof Inet4Address) // 过滤本机和非 ipv4 地址
            .toArray(InetAddress[]::new);

        for (var ip : localIPv4Addresses) {
            o.green("> 网络: " + httpOrHttps + "://" + ip.getHostAddress() + ":" + realPort + "/").ln();
        }

        o.print();
    }

}
