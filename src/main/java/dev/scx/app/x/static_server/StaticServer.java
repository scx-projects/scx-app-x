package dev.scx.app.x.static_server;

import dev.scx.app.environment.type.ConfiguredPath;
import dev.scx.http.routing.Route;
import dev.scx.http.routing.method_matcher.MethodMatcher;
import dev.scx.http.routing.path_matcher.PathMatcher;
import dev.scx.http.routing.path_matcher.TemplatePathMatcher;
import dev.scx.http.routing.request_matcher.RequestMatcher;
import dev.scx.http.routing.x.single_file.SingleFileHandler;
import dev.scx.http.routing.x.static_files.StaticFilesHandler;

import static dev.scx.app.x.static_server.StaticServerType.SINGLE_FILE;
import static dev.scx.app.x.static_server.StaticServerType.STATIC_FILES;

/// StaticServer
///
/// @author scx567888
record StaticServer(String host, String type, String route, ConfiguredPath path) {

    public RequestMatcher requestMatcher() {
        return host == null || host.isBlank() ? RequestMatcher.any() : RequestMatcher.hostIs(host);
    }

    public TemplatePathMatcher pathMatcher() {
        return PathMatcher.ofTemplate(route);
    }

    public StaticServerType staticServerType() {
        // 没写 我们默认 STATIC_FILES
        if (type == null) {
            return STATIC_FILES;
        }
        var t = type.trim().toUpperCase();
        return switch (t) {
            case "D", "DIR", "DIRECTORY", "STATIC-FILES", "STATIC_FILES" -> STATIC_FILES;
            case "F", "FILE", "SINGLE-FILE", "SINGLE_FILE" -> SINGLE_FILE;
            default -> throw new IllegalArgumentException("未知 static-server.type : " + type);
        };
    }

    public Route toRoute() {
        var handler = switch (staticServerType()) {
            case STATIC_FILES -> StaticFilesHandler.of(path.path());
            case SINGLE_FILE -> SingleFileHandler.of(path.path());
        };
        return Route.of(
            requestMatcher(),
            pathMatcher(),
            MethodMatcher.any(),
            handler
        );
    }

}
